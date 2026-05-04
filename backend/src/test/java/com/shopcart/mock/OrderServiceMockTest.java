package com.shopcart.mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
