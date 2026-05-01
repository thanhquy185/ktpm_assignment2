package com.shopcart.configs;
// package com.shopcart.configs;

// import java.io.IOException;
// import java.util.Optional;

// import org.springframework.http.HttpStatus;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
// import org.springframework.security.web.AuthenticationEntryPoint;
// import org.springframework.stereotype.Component;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.shopcart.dtos.response.RestResponse;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//     // Properties
//     private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
//     private final ObjectMapper mapper;

//     // Constructors
//     public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
//         this.mapper = mapper;
//     }

//     // Methods
//     @Override
//     public void commence(HttpServletRequest request, HttpServletResponse response,
//             AuthenticationException authException) throws IOException, ServletException {
//         this.delegate.commence(request, response, authException);
//         response.setContentType("application/json;charset=UTF-8");

//         com.shopcart.dtos.response.RestResponse<Object> restResponse = new RestResponse<>();
//         restResponse.setStatus(HttpStatus.UNAUTHORIZED);
//         // -
//         String errorMessage = Optional.ofNullable(authException.getCause())
//                 .map(Throwable::getMessage)
//                 .orElse(authException.getMessage());
//         restResponse.setError(errorMessage);
//         restResponse
//                 .setMessage("Token không hợp lệ (hết hạn, không đúng định dạng hoặc không truyền Jwt ở header...)!");

//         mapper.writeValue(response.getWriter(), restResponse);
//     }
// }