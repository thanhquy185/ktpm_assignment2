package com.shopcart.mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.FakeDataForTest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.entities.Order;
import com.shopcart.entities.Product;
import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;
import com.shopcart.repositories.OrderRepository;
import com.shopcart.services.CartService;
import com.shopcart.services.CouponService;
import com.shopcart.services.InventoryService;
import com.shopcart.services.OrderService;
import com.shopcart.services.ProductService;
import com.shopcart.services.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Mock Test")
public class OrderServiceMockTest {
        @Mock
        private UserService userService;
        @Mock
        private CartService cartService;
        @Mock
        private CouponService couponService;
        @Mock
        private ProductService productService;
        @Mock
        private InventoryService inventoryService;
        @Mock
        private OrderRepository orderRepository;
        @InjectMocks
        private OrderService orderService;

        private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

        @Test
        @DisplayName("Method: checkQuantityAndPriceBeforeOrder() - Kiểm tra số lượng và giá có hợp lệ trước khi tạo đơn hàng")
        void test_checkQuantityAndPriceBeforeOrder() {
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(30000000L)
                                                .build());

                assertDoesNotThrow(() -> this.orderService.checkQuantityAndPriceBeforeOrder(request));
        }

        @Test
        @DisplayName("Method: checkStockBeforeOrder() - Kiểm tra tồn kho sản phẩm trước khi tạo đơn hàng")
        void test_CheckStockBeforeOrder() {
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(30000000L)
                                                .build());

                when(this.productService.getProductById(productId))
                                .thenReturn(product);

                assertDoesNotThrow(() -> this.orderService.checkStockBeforeOrder(request));

                ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
                verify(this.productService, times(1))
                                .getProductById(captor.capture());
                assertEquals(productId, captor.getValue());

        }

        @Test
        @DisplayName("Method: calculateOrderTotal() - Tính tổng tiền đơn hàng")
        void test_CalculateOrderTotal() {
                Long subtotal = 100000L;
                Double discount = 10000D;
                Long shippingFee = 30000L;
                Double expected = 120000D;

                Double result = this.orderService.calculateOrderTotal(subtotal, discount, shippingFee);
                assertNotNull(result);
                assertEquals(expected, result);
        }

        @Test
        @DisplayName("TC 1: - Tạo đơn hàng thành công với OrderRepository được mock")
        void test_CreateOrder_Successful() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(100000L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                when(this.userService.getUserById(userId))
                                .thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Order result = this.orderService.createOrder(request);

                assertNotNull(result);
                verify(this.orderRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }

        @Test
        @DisplayName("TC 2: - Xác minh rằng tồn kho được giảm cho mỗi sản phẩm")
        void test_CreateOrder_VerifyStockDecreased() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(100000L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                when(this.userService.getUserById(userId))
                                .thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                this.orderService.createOrder(request);

                ArgumentCaptor<UUID> productIdCaptor = ArgumentCaptor.forClass(UUID.class);
                ArgumentCaptor<Long> quantityCaptor = ArgumentCaptor.forClass(Long.class);
                verify(this.inventoryService, times(1)).decreaseStock(productIdCaptor.capture(),
                                quantityCaptor.capture());
                assertEquals(productId, productIdCaptor.getValue());
                assertEquals(2L, quantityCaptor.getValue());
        }

        @Test
        @DisplayName("TC 3: - Xác minh rằng giỏ hàng được xóa sau khi tạo đơn")
        void test_CreateOrder_VerifyCartCleared() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(100000L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                when(this.userService.getUserById(userId))
                                .thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                this.orderService.createOrder(request);

                ArgumentCaptor<UUID> userIdCaptor = ArgumentCaptor.forClass(UUID.class);
                verify(this.cartService, times(1)).clearCart(userIdCaptor.capture());
                assertEquals(userId, userIdCaptor.getValue());
        }

        @Test
        @DisplayName("TC 4: - Tạo đơn hàng với coupon hợp lệ và kiểm tra dữ liệu")
        void test_CreateOrder_WithCoupon_VerifyData() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                UUID couponId = this.fakeDataForTest.getCouponFake1().getId();
                Product product = this.fakeDataForTest.getProductFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(100000L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(couponId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                Order expectedOrder = new Order();
                expectedOrder.setId(UUID.randomUUID());
                expectedOrder.setUser(this.fakeDataForTest.getUserFake1());
                expectedOrder.setShippingAddress("123 Main St");
                expectedOrder.setSubtotal(200000L);
                expectedOrder.setDiscount(20000D);
                expectedOrder.setShippingFee(30000L);

                when(this.userService.getUserById(userId))
                                .thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                                .thenReturn(expectedOrder);
                when(this.couponService.getCouponById(couponId))
                                .thenReturn(this.fakeDataForTest.getCouponFake1());

                Order result = this.orderService.createOrder(request);

                assertNotNull(result);
                assertEquals("123 Main St", result.getShippingAddress());
                assertEquals(200000L, result.getSubtotal());
                verify(this.couponService, times(1)).getCouponById(couponId);
        }

        @Test
        @DisplayName("TC 5: - Tạo đơn hàng không có coupon và kiểm tra dữ liệu")
        void test_CreateOrder_WithoutCoupon_VerifyData() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(100000L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                Order expectedOrder = new Order();
                expectedOrder.setId(UUID.randomUUID());
                expectedOrder.setUser(this.fakeDataForTest.getUserFake1());
                expectedOrder.setDiscount(0D);
                expectedOrder.setSubtotal(200000L);

                when(this.userService.getUserById(userId))
                                .thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                                .thenReturn(expectedOrder);

                Order result = this.orderService.createOrder(request);

                assertNotNull(result);
                assertEquals(0D, result.getDiscount());
                verify(this.couponService, times(0)).getCouponById(org.mockito.ArgumentMatchers.any(UUID.class));
        }

        @Test
        @DisplayName("TC2_CQAP: Kiểm tra danh sách chi tiết đơn hàng nhưng số lượng yêu cầu bé hơn 0")
        void test_checkQuantityAndPrice_QuantityLessThanZero() {
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(UUID.randomUUID().toString())
                                                .quantity(-1L) 
                                                .price(30000000L)
                                                .build());

                assertThrows(com.shopcart.exceptions.OrderItemQuantityGreaterThanZero.class,
                                () -> this.orderService.checkQuantityAndPriceBeforeOrder(request));
        }

        @Test
        @DisplayName("TC3_CQAP: Kiểm tra danh sách chi tiết đơn hàng nhưng số lượng yêu cầu bằng 0")
        void test_checkQuantityAndPrice_QuantityEqualsZero() {
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(UUID.randomUUID().toString())
                                                .quantity(0L) 
                                                .price(30000000L)
                                                .build());

                assertThrows(com.shopcart.exceptions.OrderItemQuantityGreaterThanZero.class,
                                () -> this.orderService.checkQuantityAndPriceBeforeOrder(request));
        }

        @Test
        @DisplayName("TC4_CQAP: Kiểm tra danh sách chi tiết đơn hàng nhưng giá sản phẩm bé hơn 0")
        void test_checkQuantityAndPrice_PriceLessThanZero() {
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(UUID.randomUUID().toString())
                                                .quantity(2L)
                                                .price(-1000L) 
                                                .build());

                assertThrows(com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero.class,
                                () -> this.orderService.checkQuantityAndPriceBeforeOrder(request));
        }

        @Test
        @DisplayName("TC7_CO: Tạo đơn hàng nhưng tồn kho của tồn kho không tồn tại")
        void test_CreateOrder_ButProductNotFoundInInventory() throws com.shopcart.exceptions.ProductNotFoundInInventory {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                product.setInventory(null); 

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .orderItems(List.of(OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L).price(30000000L).build()))
                                .build();

                when(this.productService.getProductById(productId)).thenReturn(product);

                assertThrows(com.shopcart.exceptions.ProductNotFoundInInventory.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }

        @Test
        @DisplayName("TC8_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại")
        void test_CreateOrder_ButProductNotFoundInStockCheck() throws com.shopcart.exceptions.ProductNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .orderItems(List.of(OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L).price(30000000L).build()))
                                .build();

                when(this.productService.getProductById(productId)).thenThrow(new com.shopcart.exceptions.ProductNotFound(productId));

                assertThrows(com.shopcart.exceptions.ProductNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }

        @Test
        @DisplayName("TC9_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ")
        void test_CreateOrder_ButInsufficientStock() throws com.shopcart.exceptions.InsufficientStock {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .orderItems(List.of(OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(999L).price(30000000L).build()))
                                .build();

                when(this.productService.getProductById(productId)).thenReturn(product);

                assertThrows(com.shopcart.exceptions.InsufficientStock.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }

        @Test
        @DisplayName("TC10_CO: Tạo đơn hàng nhưng người dùng không tồn tại")
        void test_CreateOrder_ButUserNotFound() throws com.shopcart.exceptions.UserNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .orderItems(List.of(OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L).price(30000000L).build()))
                                .build();

                when(this.productService.getProductById(productId)).thenReturn(product);
                when(this.userService.getUserById(userId)).thenThrow(new com.shopcart.exceptions.UserNotFound(userId));

                assertThrows(com.shopcart.exceptions.UserNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }

        @Test
        @DisplayName("TC11_CO: Tạo đơn hàng nhưng mã giảm giá không tồn tại")
        void test_CreateOrder_ButCouponNotFound() throws com.shopcart.exceptions.CouponNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                UUID couponId = UUID.randomUUID();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(couponId.toString())
                                .orderItems(List.of(OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L).price(30000000L).build()))
                                .build();

                when(this.productService.getProductById(productId)).thenReturn(product);
                when(this.userService.getUserById(userId)).thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.couponService.getCouponById(couponId)).thenThrow(new com.shopcart.exceptions.CouponNotFound(couponId));

                assertThrows(com.shopcart.exceptions.CouponNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }

        @Test
        @DisplayName("TC12_CO: Tạo đơn hàng nhưng mã giảm giá hết hạn")
        void test_CreateOrder_ButCouponOutOfDate() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                com.shopcart.entities.Coupon coupon = this.fakeDataForTest.getCouponFake1();
                UUID couponId = coupon.getId();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(couponId.toString())
                                .orderItems(List.of(OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L).price(30000000L).build()))
                                .build();

                when(this.productService.getProductById(productId)).thenReturn(product);
                when(this.userService.getUserById(userId)).thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.couponService.getCouponById(couponId)).thenReturn(coupon);
                doThrow(new com.shopcart.exceptions.CouponOutOfDate(coupon.getCode()))
                                .when(this.couponService).checkOutOfDate(
                                                org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.any());

                assertThrows(com.shopcart.exceptions.CouponOutOfDate.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Order.class));
        }
}
