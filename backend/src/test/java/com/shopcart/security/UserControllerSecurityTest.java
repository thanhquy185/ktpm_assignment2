package com.shopcart.security;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.UserController;
import com.shopcart.dtos.request.LoginRequest;
import com.shopcart.entities.User;
import com.shopcart.exceptions.UserNotFoundByUsername;
import com.shopcart.services.UserService;
import com.shopcart.utils.SecurityUtil;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("User Controller Security Tests")
public class UserControllerSecurityTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private AuthenticationManagerBuilder authenticationManagerBuilder;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @MockBean
        private SecurityUtil securityUtil;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private UserService userService;

        private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

        @Test
        @DisplayName("TC1: POST /api/users/login - Đăng nhập và trả về lỗi thành công")
        void test_Login_HaveNotError() throws Exception {
                String usernameSQLInjection = "'customer' OR 1 = 1";
                LoginRequest request = LoginRequest.builder()
                                .username(usernameSQLInjection)
                                .password("customer")
                                .build();

                when(this.userService.getUserByUsername(usernameSQLInjection))
                                .thenThrow(new UserNotFoundByUsername(usernameSQLInjection));

                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND_BY_USERNAME"))
                                .andExpect(jsonPath("$.message")
                                                .value(new UserNotFoundByUsername(usernameSQLInjection).getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));

                verify(this.userService, times(1)).getUserByUsername(usernameSQLInjection);
        }

        @Test
        @DisplayName("TC2: GET /api/users/login/unsafe - Đăng nhập và trả về danh sách người dùng")
        void test_Login_HaveError() throws Exception {
                List<User> users = List.of(this.fakeDataForTest.getUserFake1(), this.fakeDataForTest.getUserFake2());

                when(this.userService.getUserByUsernameError()).thenReturn(users);

                mockMvc.perform(get("/api/users/login/unsafe"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                                .andExpect(jsonPath("$.error").value(nullValue()))
                                .andExpect(jsonPath("$.message")
                                                .value("Get user by username sql injection is successful!"))
                                .andExpect(jsonPath("$.data.length()").value(2));

                verify(this.userService, times(1)).getUserByUsernameError();
        }
}
