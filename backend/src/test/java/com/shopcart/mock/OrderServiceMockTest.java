package com.shopcart.mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

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
import com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero;
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
        @DisplayName("Tạo đơn hàng thành công")
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
        @DisplayName("Tạo đơn hàng nhưng sản phẩm không tồn tại")
        void test_CreateOrder_ProductNotFound() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = UUID.randomUUID();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(1L)
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

                when(this.productService.getProductById(productId))
                                .thenThrow(new com.shopcart.exceptions.ProductNotFound(productId));

                assertThrows(com.shopcart.exceptions.ProductNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.productService, times(1)).getProductById(productId);
                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0")
        void test_CreateOrder_QuantityLessThanZero() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(-1L)
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

                assertThrows(com.shopcart.exceptions.OrderItemQuantityGreaterThanZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("Tạo đơn hàng nhưng số lượng sản phẩm bằng 0")
        void test_CreateOrder_QuantityIsZero() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(0L)
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

                assertThrows(com.shopcart.exceptions.OrderItemQuantityGreaterThanZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0")
        void test_CreateOrder_PriceLessThanZero() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(1L)
                                                .price(-1L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                assertThrows(com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC6_CO: Tạo đơn hàng nhưng giá bán sản phẩm bằng 0")
        void test_CreateOrder_PriceIsZero() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                        .userId(userId.toString())
                        .shippingAddress("123 Main St")
                        .shippingMethod(OrderShippingMethodEnum.STANDARD)
                        .shippingFee(30000L)
                        .paymentMethod(OrderPaymentMethodEnum.COD)
                        .orderItems(List.of(
                                OrderItemRequest.builder()
                                        .productId(productId.toString())
                                        .quantity(1L)
                                        .price(0L) // Giá bằng 0
                                        .build()))
                        .build();

                
                assertThrows(OrderItemPriceGreaterThanOrEqualZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }
}
