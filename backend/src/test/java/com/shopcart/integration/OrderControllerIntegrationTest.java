package com.shopcart.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.OrderController;
import com.shopcart.services.OrderService;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Order Controller Integration Tests")
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private OrderService orderService;

    private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

    @Test
    @DisplayName("POST /api/orders - Tạo đơn hàng")
    void test_CreateOrder() throws Exception {
        
    }

    @Test
    @DisplayName("DELETE /api/orders - Huỷ đơn hàng")
    void test_CancelOrder() throws Exception {
        
    }

    // Hàm hỗ trợ tạo Request giả đồng bộ với FakeDataForTest
    private OrderCreateRequest buildMockOrderRequest() {
        return OrderCreateRequest.builder()
            .userId(fakeDataForTest.getUserIdFake1().toString())
            .shippingAddress("123 Main St")
            .shippingMethod(OrderShippingMethodEnum.STANDARD)
            .shippingFee(30000L)
            .paymentMethod(OrderPaymentMethodEnum.COD)
            .orderItems(List.of(
                OrderItemRequest.builder()
                    .productId(fakeDataForTest.getProductIdFake1().toString())
                    .quantity(2L)
                    .price(100000L)
                    .build()))
            .build();
    }

    @Test
    @DisplayName("TC7_CO: POST /api/orders - Tạo đơn hàng nhưng tồn kho không tồn tại (404 Not Found)")
    void test_CreateOrder_ProductNotFoundInInventory() throws Exception {
        OrderCreateRequest request = buildMockOrderRequest();
        UUID productId = fakeDataForTest.getProductIdFake1();

        when(this.orderService.createOrder(any(OrderCreateRequest.class)))
            .thenThrow(new com.shopcart.exceptions.ProductNotFoundInInventory(productId));

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("PRODUCT_NOT_FOUND_IN_INVENTORY")));
    }

    @Test
    @DisplayName("TC8_CO: POST /api/orders - Tạo đơn hàng nhưng sản phẩm không tồn tại (404 Not Found)")
    void test_CreateOrder_ProductNotFound() throws Exception {
        OrderCreateRequest request = buildMockOrderRequest();
        UUID productId = fakeDataForTest.getProductIdFake1();

        when(this.orderService.createOrder(any(OrderCreateRequest.class)))
            .thenThrow(new com.shopcart.exceptions.ProductNotFound(productId));

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("PRODUCT_NOT_FOUND")));
    }

    @Test
    @DisplayName("TC9_CO: POST /api/orders - Tạo đơn hàng nhưng tồn kho không đủ (400 Bad Request)")
    void test_CreateOrder_InsufficientStock() throws Exception {
        OrderCreateRequest request = buildMockOrderRequest();
        UUID productId = fakeDataForTest.getProductIdFake1();

        when(this.orderService.createOrder(any(OrderCreateRequest.class)))
            .thenThrow(new com.shopcart.exceptions.InsufficientStock(productId));

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("INSUFFICIENT_STOCK")));
    }

    @Test
    @DisplayName("TC10_CO: POST /api/orders - Tạo đơn hàng nhưng người dùng không tồn tại (Unhandled Exception)")
    void test_CreateOrder_UserNotFound() throws Exception {
        OrderCreateRequest request = buildMockOrderRequest();
        UUID userId = fakeDataForTest.getUserIdFake1();

        when(this.orderService.createOrder(any(OrderCreateRequest.class)))
            .thenThrow(new com.shopcart.exceptions.UserNotFound(userId));

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
            jakarta.servlet.ServletException.class,
            () -> mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(request)))
        );

        org.junit.jupiter.api.Assertions.assertTrue(
            exception.getCause() instanceof com.shopcart.exceptions.UserNotFound
        );
    }

    @Test
    @DisplayName("TC11_CO: POST /api/orders - Tạo đơn hàng nhưng mã giảm giá không tồn tại (404 Not Found)")
    void test_CreateOrder_CouponNotFound() throws Exception {
        OrderCreateRequest request = buildMockOrderRequest();
        UUID couponId = UUID.randomUUID();
        request.setCouponId(couponId.toString());

        when(this.orderService.createOrder(any(OrderCreateRequest.class)))
            .thenThrow(new com.shopcart.exceptions.CouponNotFound(couponId));

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("COUPON_NOT_FOUND")));
    }

    @Test
    @DisplayName("TC12_CO: POST /api/orders - Tạo đơn hàng nhưng mã giảm giá hết hạn (404 Not Found)")
    void test_CreateOrder_CouponOutOfDate() throws Exception {
        OrderCreateRequest request = buildMockOrderRequest();
        String expiredCouponCode = "SUMMER_SALE";
        request.setCouponId(UUID.randomUUID().toString());

        when(this.orderService.createOrder(any(OrderCreateRequest.class)))
            .thenThrow(new com.shopcart.exceptions.CouponOutOfDate(expiredCouponCode));

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("COUPON_OUT_OF_DATE")));
    }
}