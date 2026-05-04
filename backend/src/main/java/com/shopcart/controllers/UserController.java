package com.shopcart.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dtos.request.LoginRequest;
import com.shopcart.dtos.request.RestLoginDTO;
import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.User;
import com.shopcart.exceptions.InvalidAccessToken;
import com.shopcart.exceptions.UserNotFoundByUsername;
import com.shopcart.services.UserService;
import com.shopcart.utils.SecurityUtil;
import com.shopcart.utils.ValidationUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final PasswordEncoder passwordEncoder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        @Value("${jwt.refresh-token-validity-in-seconds}")
        private Long jwtRefreshTokenExpiration;

        @GetMapping("/info")
        public ResponseEntity<?> getInfo() {
                String username = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : null;
                User currentUser = this.userService.getUserByUsername(username);

                RestResponse<User> restResponse = RestResponse.<User>builder()
                                .status(HttpStatus.OK.value())
                                .message("Get info current user successful!")
                                .data(currentUser)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @PostMapping("/login")
        public ResponseEntity<?> handleLogin(
                        @RequestBody @Valid LoginRequest request,
                        BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ValidationUtil.buildRestResponse(bindingResult));
                }
                if (userService.getUserByUsername(request.getUsername()) == null
                                || !passwordEncoder.matches(
                                                request.getPassword(),
                                                userService.getUserByUsername(request.getUsername()).getPassword())) {
                        // RestResponse<User> restResponse = RestResponse.<User>builder()
                        // .status(HttpStatus.NOT_FOUND.value())
                        // .message(new UserNotFoundByUsername(request.getUsername()).getMessage())
                        // .error("USER_NOT_FOUND_BY_USERNAME")
                        // .build();

                        // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
                }

                // Nạp input vào security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                request.getUsername(), request.getPassword());

                // Xác thực người dùng
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // Nạp thông tin
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Tạo Rest Login DTO
                RestLoginDTO restLogin = new RestLoginDTO();
                User currentUser = this.userService.getUserByUsername(request.getUsername());
                if (currentUser != null) {
                        RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();
                        userLogin.setId(currentUser.getId());
                        userLogin.setRole(currentUser.getRole());
                        userLogin.setUsername(currentUser.getUsername());

                        restLogin.setUserLogin(userLogin);
                }
                restLogin.setAccessToken(this.securityUtil.createAccessToken(request.getUsername(), restLogin));

                // Tạo refresh token
                String refreshToken = this.securityUtil.createRefreshToken(request.getUsername(), restLogin);
                this.userService.changeRefreshToken(currentUser.getUsername(), refreshToken);

                // Tạo cookie
                ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(false) // Chỉ cho phép phía server được sử dụng (tạm cho client-web)
                                .secure(true) // Chỉ cho phép https
                                .path("/") // Cho phép tất cả đường dẫn
                                .sameSite("None") // quan trọng để cookie gửi qua cross-site
                                .maxAge(jwtRefreshTokenExpiration * 365) //
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                                responseCookie.toString()).body(restLogin);
        }

        @PostMapping("/logout")
        public ResponseEntity<?> handleLogout() throws InvalidAccessToken {
                String username = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (username.equals("")) {
                        throw new InvalidAccessToken();
                }
                this.userService.changeRefreshToken(username, null);

                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refreshToken", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }

        @ExceptionHandler(UserNotFoundByUsername.class)
        public ResponseEntity<?> handleUserNotFoundByUsername(UserNotFoundByUsername e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("USER_NOT_FOUND_BY_USERNAME")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
        }

        @ExceptionHandler(InvalidAccessToken.class)
        public ResponseEntity<?> handleInvalidAccessToken(InvalidAccessToken e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("USER_NOT_FOUND_BY_USERNAME")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
        }
}
