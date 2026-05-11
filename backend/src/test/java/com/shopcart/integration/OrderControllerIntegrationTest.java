package com.shopcart.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
<<<<<<< HEAD
import org.springframework.http.HttpStatus;
=======
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

>>>>>>> 540819b (Refactored integration tests and renamed related files.)
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.OrderController;
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.dtos.request.OrderCancelRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.entities.Order;
import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;
import com.shopcart.enums.OrderStatusEnum;
import com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero;
import com.shopcart.exceptions.OrderItemQuantityGreaterThanZero;
import com.shopcart.exceptions.OrderNotFound;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.services.OrderService;
import com.shopcart.exceptions.CouponNotFound;
import com.shopcart.exceptions.CouponOutOfDate;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.OrderAlreadyCancelled;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.exceptions.UserNotFound;

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

<<<<<<< HEAD
        // Hàm hỗ trợ tạo Request giả để tái sử dụng cho các TC
        private OrderCreateRequest buildMockOrderRequest() {
                return OrderCreateRequest.builder()
                                .userId(UUID.randomUUID().toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(UUID.randomUUID().toString())
                                                                .quantity(2L)
                                                                .price(100000L)
                                                                .build()))
                                .build();
        }

        @Test
        @DisplayName("TC1_CO: Tạo đơn hàng thành công")
        void test_CreateOrder_Successful() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(5000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(1L)
                                                                .price(10000L)
                                                                .build()))
                                .build();

                Order order = this.fakeDataForTest.getOrderFake1();

                when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(order);

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.id").value(order.getId().toString()));
        }

        @Test
        @DisplayName("TC2_CO: Tạo đơn hàng nhưng sản phẩm không tồn tại")
        void test_CreateOrder_ProductNotFound() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = UUID.randomUUID();
                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(5000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(1L)
                                                                .price(10000L)
                                                                .build()))
                                .build();

                when(orderService.createOrder(any(OrderCreateRequest.class))).thenThrow(new ProductNotFound(productId));

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC3_CO: Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0")
        void test_CreateOrder_QuantityLessThanZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(5000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(-1L)
                                                                .price(10000L)
                                                                .build()))
                                .build();

                when(orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new OrderItemQuantityGreaterThanZero());

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC4_CO: Tạo đơn hàng nhưng số lượng sản phẩm bằng 0")
        void test_CreateOrder_QuantityIsZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(5000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(0L)
                                                                .price(10000L)
                                                                .build()))
                                .build();

                when(orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new OrderItemQuantityGreaterThanZero());

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC5_CO: Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0")
        void test_CreateOrder_PriceLessThanZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(5000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(1L)
                                                                .price(-10000L)
                                                                .build()))
                                .build();

                when(orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new OrderItemPriceGreaterThanOrEqualZero());

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC6_CO: Tạo đơn hàng nhưng giá bán sản phẩm bằng 0")
        void test_CreateOrder_PriceIsZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(5000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(1L)
                                                                .price(0L)
                                                                .build()))
                                .build();

                Order order = this.fakeDataForTest.getOrderFake1();

                when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(order);

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.id").value(order.getId().toString()));
        }

        @Test
        @DisplayName("TC7_CO: POST /api/orders - Tạo đơn hàng nhưng tồn kho không tồn tại (404 Not Found)")
        void test_CreateOrder_ProductNotFoundInInventory() throws Exception {
                OrderCreateRequest request = buildMockOrderRequest();

                when(this.orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new ProductNotFoundInInventory(UUID.randomUUID()));

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound()) // Expect 404
                                .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND_IN_INVENTORY"));
        }

        @Test
        @DisplayName("TC8_CO: POST /api/orders - Tạo đơn hàng nhưng tồn kho không đủ (400 Bad Request)")
        void test_CreateOrder_InsufficientStock() throws Exception {
                OrderCreateRequest request = buildMockOrderRequest();

                when(this.orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new InsufficientStock(UUID.randomUUID()));

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("INSUFFICIENT_STOCK"));
        }

        @Test
        @DisplayName("TC9_CO: POST /api/orders - Tạo đơn hàng nhưng người dùng không tồn tại (Unhandled Exception)")
        void test_CreateOrder_UserNotFound() throws Exception {
                OrderCreateRequest request = buildMockOrderRequest();
                UUID fakeUserId = UUID.randomUUID();

                when(this.orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new UserNotFound(fakeUserId));

                Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
                                jakarta.servlet.ServletException.class,
                                () -> mockMvc.perform(post("/api/orders")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(this.objectMapper.writeValueAsString(request))));

                org.junit.jupiter.api.Assertions.assertTrue(
                                exception.getCause() instanceof UserNotFound);
        }

        @Test
        @DisplayName("TC10_CO: POST /api/orders - Tạo đơn hàng nhưng mã giảm giá không tồn tại (404 Not Found)")
        void test_CreateOrder_CouponNotFound() throws Exception {
                OrderCreateRequest request = buildMockOrderRequest();
                request.setCouponId(UUID.randomUUID().toString());

                when(this.orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new CouponNotFound(UUID.randomUUID()));

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("COUPON_NOT_FOUND"));
        }

        @Test
        @DisplayName("TC11_CO: POST /api/orders - Tạo đơn hàng nhưng mã giảm giá hết hạn (404 Not Found)")
        void test_CreateOrder_CouponOutOfDate() throws Exception {
                OrderCreateRequest request = buildMockOrderRequest();
                request.setCouponId(UUID.randomUUID().toString());

                when(this.orderService.createOrder(any(OrderCreateRequest.class)))
                                .thenThrow(new CouponOutOfDate("SUMMER_SALE"));

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("COUPON_OUT_OF_DATE"));
        }

        @Test
        @DisplayName("TC1_CO: DELETE /api/orders - Huỷ đơn hàng thành công")
        void cancelOrder_WhenValidRequest_ShouldReturnCancelledOrder() throws Exception {

                Order cancelledOrder = fakeDataForTest.getOrderFake1();
                cancelledOrder.setStatus(OrderStatusEnum.CANCELLED);

                OrderCancelRequest request = OrderCancelRequest.builder()
                                .orderId(cancelledOrder.getId().toString())
                                .build();

                when(orderService.cancelOrder(any(OrderCancelRequest.class)))
                                .thenReturn(cancelledOrder);

                mockMvc.perform(delete("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())

                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.OK.value()))

                                .andExpect(jsonPath("$.message")
                                                .value("Cancel order is successful!"))

                                .andExpect(jsonPath("$.data.id")
                                                .value(cancelledOrder.getId().toString()))

                                .andExpect(jsonPath("$.data.status")
                                                .value(OrderStatusEnum.CANCELLED.getDescription()));
        }

        @Test
        @DisplayName("TC2_CO: DELETE /api/orders - Huỷ đơn hàng nhưng đơn hàng của người dùng không tồn tại")
        void cancelOrder_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {

                UUID orderId = UUID.randomUUID();

                OrderCancelRequest request = OrderCancelRequest.builder()
                                .orderId(orderId.toString())
                                .build();

                when(orderService.cancelOrder(any(OrderCancelRequest.class)))
                                .thenThrow(new OrderNotFound(orderId));

                mockMvc.perform(delete("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())

                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.NOT_FOUND.value()))

                                .andExpect(jsonPath("$.error")
                                                .value("ORDER_NOT_FOUND"))

                                .andExpect(jsonPath("$.message")
                                                .exists());
        }

        @Test
        @DisplayName("TC3_CO: DELETE /api/orders - Huỷ đơn hàng nhưng đơn hàng đã được huỷ từ trước")
        void cancelOrder_WhenOrderAlreadyCancelled_ShouldReturnBadRequest() throws Exception {

                UUID orderId = fakeDataForTest.getOrderIdFake1();

                OrderCancelRequest request = OrderCancelRequest.builder()
                                .orderId(orderId.toString())
                                .build();

                when(orderService.cancelOrder(any(OrderCancelRequest.class)))
                                .thenThrow(new OrderAlreadyCancelled(orderId));

                mockMvc.perform(delete("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())

                                .andExpect(jsonPath("$.status")
                                                .value(HttpStatus.BAD_REQUEST.value()))

                                .andExpect(jsonPath("$.error")
                                                .value("ORDER_ALREADY_CANCELLED"))

                                .andExpect(jsonPath("$.message")
                                                .exists());
        }
}
=======
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
>>>>>>> 540819b (Refactored integration tests and renamed related files.)
