package com.shopcart.mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.FakeDataForTest;
import com.shopcart.dtos.request.OrderCancelRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.entities.Order;
import com.shopcart.entities.Product;
import com.shopcart.entities.User;
import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;
import com.shopcart.enums.OrderStatusEnum;
import com.shopcart.exceptions.CouponNotFound;
import com.shopcart.exceptions.CouponOutOfDate;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.OrderAlreadyCancelled;
import com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero;
import com.shopcart.exceptions.OrderItemQuantityGreaterThanZero;
import com.shopcart.exceptions.OrderNotFound;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.exceptions.UserNotFound;
import com.shopcart.exceptions.UserNotFoundInCart;
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
        @DisplayName("TC1_GOS: Truy danh sách đơn hàng thành công")
        void test_GetOrders_Successful() {
                UUID orderId1 = this.fakeDataForTest.getOrderIdFake1();
                UUID orderId2 = this.fakeDataForTest.getOrderIdFake2();
                List<Order> orders = this.fakeDataForTest.getOrdersFake();

                when(this.orderRepository.findAll()).thenReturn(orders);

                List<Order> result = this.orderRepository.findAll();
                assertNotNull(result);
                assertEquals(result.isEmpty(), false);
                assertEquals(2, result.size());
                assertEquals(orderId1, result.get(0).getId());
                assertEquals(orderId2, result.get(1).getId());

                verify(this.orderRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("TC2_GOS: Truy danh sách đơn hàng thành công (danh sách rỗng)")
        void test_GetOrders_SuccessfulButEmpty() {
                List<Order> orders = List.of();

                when(this.orderRepository.findAll()).thenReturn(orders);

                List<Order> result = this.orderRepository.findAll();
                assertNotNull(result);
                assertEquals(result.isEmpty(), true);
                assertEquals(0, result.size());

                verify(this.orderRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("TC1_GOBID: Truy một đơn hàng theo mã đơn hàng thành công")
        void test_GetOrderById_Successful() {
                UUID orderId = this.fakeDataForTest.getOrderIdFake1();
                Order order = this.fakeDataForTest.getOrderFake1();

                when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

                Order result = this.orderRepository.findById(orderId).get();

                ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
                verify(this.orderRepository, times(1)).findById(captor.capture());
                assertNotNull(captor.getValue());
                assertEquals(result.getId(), captor.getValue());

        }

        @Test
        @DisplayName("TC2_GOBID: Truy một đơn hàng theo mã đơn hàng nhưng mã đơn hàng không tồn tại")
        void test_GetOrderById_OrderNotFound() throws OrderNotFound {
                UUID orderId = UUID.randomUUID();

                when(this.orderRepository.findById(orderId)).thenThrow(new OrderNotFound(orderId));

                assertThrows(OrderNotFound.class, () -> {
                        this.orderRepository.findById(orderId);
                });
        }

        @Test
        @DisplayName("TC1_GOSBUD: Truy danh sách đơn hàng theo mã người dùng thành công")
        void test_GetOrdersByUserId_Successful() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                Order order = this.fakeDataForTest.getOrderFake1();
                order.setUser(User.builder().id(userId).build());
                List<Order> orders = List.of(order);

                when(this.orderRepository.findByUserId(userId)).thenReturn(orders);

                List<Order> result = this.orderRepository.findByUserId(userId);

                ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
                verify(this.orderRepository, times(1))
                                .findByUserId(captor.capture());
                assertEquals(result.isEmpty(), false);
                assertEquals(result.get(0).getUser().getId(), captor.getValue());
        }

        @Test
        @DisplayName("TC2_GOSBUD: Truy danh sách đơn hàng theo mã người dùng thành công (danh sách rỗng)")
        void test_GetOrdersByUserId_SuccessfulEmpty() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                List<Order> orders = List.of();

                when(this.orderRepository.findByUserId(userId)).thenReturn(orders);

                List<Order> result = this.orderRepository.findByUserId(userId);

                ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
                verify(this.orderRepository, times(1))
                                .findByUserId(captor.capture());
                assertEquals(result.isEmpty(), true);
        }

        @Test
        @DisplayName("TC3_GOSBUD: Truy danh sách đơn hàng theo mã người dùng nhưng mã người dùng không tồn tại")
        void test_GetOrdersByUserId_UserNotFoundInCart() throws UserNotFoundInCart {
                UUID userId = UUID.randomUUID();

                when(this.orderRepository.findByUserId(userId))
                                .thenThrow(new UserNotFoundInCart(userId));

                assertThrows(UserNotFoundInCart.class, () -> {
                        this.orderRepository.findByUserId(userId);
                });

                verify(this.orderRepository, times(1)).findByUserId(userId);
        }

        @Test
        @DisplayName("TC1_CSBO: Kiểm tra tồn kho sản phẩm thành công")
        void test_CheckStockBeforeOrder_Successful() {
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
        @DisplayName("TC2_CSBO: Kiểm tra tồn kho sản phẩm nhưng sản phẩm không tồn tại")
        void test_CheckStockBeforeOrder_ProductNotFound() throws ProductNotFound {
                UUID productId = UUID.randomUUID();
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(30000000L)
                                                .build());

                when(this.productService.getProductById(productId))
                                .thenThrow(new ProductNotFound(productId));

                assertThrows(ProductNotFound.class, () -> {
                        this.orderService.checkStockBeforeOrder(request);
                });

                verify(this.productService, times(1))
                                .getProductById(productId);
        }

        @Test
        @DisplayName("TC3_CSBO: Kiểm tra tồn kho sản phẩm nhưng tồn kho của sản phẩm không tồn tại")
        void test_CheckStockBeforeOrder_ProductNotFoundInInventory() throws ProductNotFoundInInventory {
                UUID productId = UUID.randomUUID();
                Product product = this.fakeDataForTest.getProductFake1();
                product.setInventory(null);
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .price(30000000L)
                                                .build());

                when(this.productService.getProductById(productId))
                                .thenReturn(product);

                assertThrows(ProductNotFoundInInventory.class, () -> {
                        this.orderService.checkStockBeforeOrder(request);
                });

                verify(this.productService, times(1))
                                .getProductById(productId);
        }

        @Test
        @DisplayName("TC4_CSBO: Kiểm tra tồn kho sản phẩm nhưng tồn kho của sản phẩm không đủ")
        void test_CheckStockBeforeOrder_InsufficientStock() throws InsufficientStock {
                UUID productId = UUID.randomUUID();
                Product product = this.fakeDataForTest.getProductFake1();
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(99L)
                                                .price(30000000L)
                                                .build());

                when(this.productService.getProductById(productId))
                                .thenReturn(product);

                assertThrows(InsufficientStock.class, () -> {
                        this.orderService.checkStockBeforeOrder(request);
                });

                verify(this.productService, times(1))
                                .getProductById(productId);
        }

        @Test
        @DisplayName("TC1_COT: Tính tổng tiền đơn hàng thành công")
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
        @DisplayName("TC1_CO: Tạo đơn hàng thành công")
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
                when(this.orderRepository.save(any(Order.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Order result = this.orderService.createOrder(request);

                assertNotNull(result);
                verify(this.orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC2_CO: Tạo đơn hàng nhưng sản phẩm không tồn tại")
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
                                .thenThrow(new ProductNotFound(productId));

                assertThrows(ProductNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.productService, times(1)).getProductById(productId);
                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC3_CO: Tạo đơn hàng nhưng số lượng sản phẩm bé hơn 0")
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

                assertThrows(OrderItemQuantityGreaterThanZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC4_CO: Tạo đơn hàng nhưng số lượng sản phẩm bằng 0")
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

                assertThrows(OrderItemQuantityGreaterThanZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC5_CO: Tạo đơn hàng nhưng giá bán sản phẩm bé hơn 0")
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

                assertThrows(OrderItemPriceGreaterThanOrEqualZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC6_CO: Tạo đơn hàng nhưng giá bán sản phẩm bằng 0")
        void test_CreateOrder_PriceIsZero() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                List<OrderItemRequest> orderItems = List.of(
                                OrderItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(1L)
                                                .price(0L)
                                                .build());

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .shippingAddress("123 Main St")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(30000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(orderItems)
                                .build();

                assertThrows(OrderItemPriceGreaterThanOrEqualZero.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("TC1_CQAP: Kiểm tra danh sách chi tiết đơn hàng thành công")
        void test_CheckQuantityAndPriceBeforeOrder() {
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
        @DisplayName("TC2_CQAP: Kiểm tra danh sách chi tiết đơn hàng nhưng số lượng yêu cầu bé hơn 0")
        void test_checkQuantityAndPrice_QuantityLessThanZero() {
                List<OrderItemRequest> request = List.of(
                                OrderItemRequest.builder()
                                                .productId(UUID.randomUUID().toString())
                                                .quantity(-1L)
                                                .price(30000000L)
                                                .build());

                assertThrows(OrderItemQuantityGreaterThanZero.class,
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

                assertThrows(OrderItemQuantityGreaterThanZero.class,
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

                assertThrows(OrderItemPriceGreaterThanOrEqualZero.class,
                                () -> this.orderService.checkQuantityAndPriceBeforeOrder(request));
        }

        @Test
        @DisplayName("TC7_CO: Tạo đơn hàng nhưng tồn kho của tồn kho không tồn tại")
        void test_CreateOrder_ButProductNotFoundInInventory()
                        throws ProductNotFoundInInventory {
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

                assertThrows(ProductNotFoundInInventory.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC8_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ")
        void test_CreateOrder_ButInsufficientStock() throws InsufficientStock {
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

                assertThrows(InsufficientStock.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC9_CO: Tạo đơn hàng nhưng người dùng không tồn tại")
        void test_CreateOrder_ButUserNotFound() throws UserNotFound {
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
                when(this.userService.getUserById(userId)).thenThrow(new UserNotFound(userId));

                assertThrows(UserNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC10_CO: Tạo đơn hàng nhưng mã giảm giá không tồn tại")
        void test_CreateOrder_ButCouponNotFound() throws CouponNotFound {
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
                when(this.couponService.getCouponById(couponId))
                                .thenThrow(new CouponNotFound(couponId));

                assertThrows(CouponNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC11_CO: Tạo đơn hàng nhưng mã giảm giá hết hạn")
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
                doThrow(new CouponOutOfDate(coupon.getCode()))
                                .when(this.couponService).checkOutOfDate(
                                                any(),
                                                any(),
                                                any(),
                                                any());

                assertThrows(CouponOutOfDate.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC1_CO: Huỷ đơn hàng thành công")
        void cancelOrder_When_OrderExists_Should_CancelSuccessfully() {
                // Arrange
                Order order = fakeDataForTest.getOrderFake1();
                order.setStatus(OrderStatusEnum.PENDING);
                OrderCancelRequest request = OrderCancelRequest.builder()
                                .orderId(order.getId().toString())
                                .build();
                when(orderRepository.findById(order.getId()))
                                .thenReturn(Optional.of(order));
                when(orderRepository.save(any(Order.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                Order result = orderService.cancelOrder(request);

                // Assert
                assertEquals(OrderStatusEnum.CANCELLED, result.getStatus());

                verify(orderRepository)
                                .findById(order.getId());
                order.getOrderItems().forEach(orderItem -> verify(inventoryService)
                                .increaseStock(
                                                orderItem.getProduct().getId(),
                                                orderItem.getQuantity()));
                verify(inventoryService, times(order.getOrderItems().size()))
                                .increaseStock(any(UUID.class), anyLong());
                verifyNoMoreInteractions(inventoryService);
                verify(orderRepository)
                                .save(order);
        }

        @Test
        @DisplayName("TC2_CO: Huỷ đơn hàng nhưng đơn hàng của người dùng không tồn tại")
        void cancelOrder_When_UserOrderDoesNotExist_Should_ThrowNotFoundException() {
                // Arrange
                UUID orderId = UUID.randomUUID();
                OrderCancelRequest request = OrderCancelRequest.builder()
                                .orderId(orderId.toString())
                                .build();
                when(orderRepository.findById(orderId))
                                .thenReturn(Optional.empty());
                // Act + Assert
                assertThrows(
                                OrderNotFound.class,
                                () -> orderService.cancelOrder(request));
                verify(orderRepository)
                                .findById(orderId);
                verify(orderRepository, never())
                                .save(any(Order.class));
                verifyNoInteractions(inventoryService);
        }

        @Test
        @DisplayName("TC3_CO: Huỷ đơn hàng nhưng đơn hàng đã được huỷ từ trước")
        void cancelOrder_When_OrderAlreadyCancelled_Should_ThrowBusinessException() {
                // Arrange
                Order order = fakeDataForTest.getOrderFake1();
                order.setStatus(OrderStatusEnum.CANCELLED);
                OrderCancelRequest request = OrderCancelRequest.builder()
                                .orderId(order.getId().toString())
                                .build();
                when(orderRepository.findById(order.getId()))
                                .thenReturn(Optional.of(order));
                // Act + Assert
                assertThrows(
                                OrderAlreadyCancelled.class,
                                () -> orderService.cancelOrder(request));
                verify(orderRepository)
                                .findById(order.getId());
                verify(orderRepository, never())
                                .save(any(Order.class));
                verifyNoInteractions(inventoryService);
        }

        @Test
        @DisplayName("TC1_GOUBI: Truy danh sách đơn hàng theo mã người dùng thành công")
        void getOrdersByUserId_WhenUserExists_ShouldReturnOrders() {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                List<Order> expectedOrders = List.of(
                                fakeDataForTest.getOrderFake1(),
                                fakeDataForTest.getOrderFake2());
                when(orderRepository.findByUserId(userId))
                                .thenReturn(expectedOrders);

                // Act
                List<Order> result = orderService.getOrdersByUserId(userId);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.size());
                assertEquals(expectedOrders, result);
                assertEquals(
                                fakeDataForTest.getOrderFake1().getId(),
                                result.get(0).getId());
                assertEquals(
                                fakeDataForTest.getOrderFake2().getId(),
                                result.get(1).getId());
                verify(orderRepository)
                                .findByUserId(userId);
                verifyNoMoreInteractions(orderRepository);
        }

        @Test
        @DisplayName("TC2_GOUBI: Truy danh sách đơn hàng theo mã người dùng thành công (danh sách rỗng)")
        void getOrdersByUserId_WhenUserHasNoOrders_ShouldReturnEmptyList() {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                when(orderRepository.findByUserId(userId))
                                .thenReturn(Collections.emptyList());

                // Act
                List<Order> result = orderService.getOrdersByUserId(userId);

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty());
                verify(orderRepository)
                                .findByUserId(userId);
                verifyNoMoreInteractions(orderRepository);
        }

        @Test
        @DisplayName("TC3_GOUBI: Truy danh sách đơn hàng theo mã người dùng nhưng mã người dùng không tồn tại")
        void getOrdersByUserId_WhenUserNotFound_ShouldReturnEmptyList() {
                // Arrange
                UUID userId = UUID.randomUUID();
                when(orderRepository.findByUserId(userId))
                                .thenReturn(Collections.emptyList());

                // Act
                List<Order> result = orderService.getOrdersByUserId(userId);

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty());
                verify(orderRepository)
                                .findByUserId(userId);
                verifyNoMoreInteractions(orderRepository);
        }
}
