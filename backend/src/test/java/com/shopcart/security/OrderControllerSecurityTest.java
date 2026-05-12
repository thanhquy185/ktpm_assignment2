package com.shopcart.security;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.OrderController;
import com.shopcart.entities.Order;
import com.shopcart.entities.User;
import com.shopcart.exceptions.OrdersAccessDenied;
import com.shopcart.services.OrderService;
import com.shopcart.services.UserService;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Order Controller Security Tests")
public class OrderControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

    @Test
    @DisplayName("TC1: GET /api/users/login - Truy danh sách đơn hàng của người khác và trả về lỗi thành công")
    void test_getOrdersByUserId_HaveNotError() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        User user = this.fakeDataForTest.getUserFake1();

        when(userService.getUserByUsername(any()))
                .thenReturn(user);
        when(this.orderService.getOrdersByUserId(eq(userId)))
                .thenThrow(new OrdersAccessDenied(userId.toString()));

        mockMvc.perform(get("/api/orders/{userId}", userId.toString())
                .with(jwt()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.error").value("ORDERS_ACCESS_DENIED"))
                .andExpect(jsonPath("$.message")
                        .value(new OrdersAccessDenied(userId.toString()).getMessage()))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(this.orderService, times(1)).getOrdersByUserId(userId);
    }

    @Test
    @DisplayName("TC2: GET /api/users/login/unsafe - Truy danh sách đơn hàng của người khác và trả về lỗi thành công")
    void test_getOrdersByUserId_HaveError() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        User user = this.fakeDataForTest.getUserFake1();
        List<Order> orders = this.fakeDataForTest.getOrdersFake();

        when(userService.getUserByUsername(any()))
                .thenReturn(user);
        when(this.orderService.getOrdersByUserId(eq(userId)))
                .thenReturn(orders);

        mockMvc.perform(get("/api/orders/{userId}/unsafe", userId.toString())
                .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.message")
                        .value("Get orders by user id is successful!"))
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(this.orderService, times(1)).getOrdersByUserId(userId);
    }
}
