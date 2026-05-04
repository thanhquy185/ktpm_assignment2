package com.shopcart.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.FakeDataForTest;
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.entities.Coupon;
import com.shopcart.entities.Order;
import com.shopcart.entities.Product;
import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero;
import com.shopcart.exceptions.OrderItemQuantityGreaterThanZero;
import com.shopcart.exceptions.OrderNotFound;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.repositories.OrderRepository;
import com.shopcart.services.CartService;
import com.shopcart.services.CouponService;
import com.shopcart.services.InventoryService;
import com.shopcart.services.OrderService;
import com.shopcart.services.ProductService;
import com.shopcart.services.UserService;

import java.time.LocalDate;

@DisplayName("Cart Service Unit Tests")
@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
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
        @DisplayName("TC1_GOBI: Tìm kiếm đơn hàng thành công")
        void test_GetOrderById_Successful() {
                UUID orderId = this.fakeDataForTest.getOrderIdFake1();
                Order order = this.fakeDataForTest.getOrderFake1();

                when(this.orderRepository.findById(orderId))
                                .thenReturn(Optional.of(order));

                Order orderSelected = this.orderService.getOrderById(orderId);
                assertNotNull(orderSelected);
                assertEquals(order.getId(), orderSelected.getId());
                assertEquals(order.getUser(), orderSelected.getUser());
                assertEquals(order.getCoupon(), orderSelected.getCoupon());
                assertEquals(order.getShippingAddress(), orderSelected.getShippingAddress());
                assertEquals(order.getSubtotal(), orderSelected.getSubtotal());
                assertEquals(order.getShippingFee(), orderSelected.getShippingFee());
                assertEquals(order.getDiscount(), orderSelected.getDiscount());
                assertEquals(order.getTotalPrice(), orderSelected.getTotalPrice());
                assertEquals(order.getStatus(), orderSelected.getStatus());

                verify(this.orderRepository, times(1)).findById(orderId);
        }

        @Test
        @DisplayName("TC2_GOBI: Tìm kiếm đơn hàng nhưng đơn hàng không tồn tại")
        void test_GetOrderById_ButOrderNotFound() throws OrderNotFound {
                UUID orderId = this.fakeDataForTest.getOrderIdFake1();

                when(this.orderRepository.findById(orderId))
                                .thenThrow(new OrderNotFound(orderId));

                assertThrows(OrderNotFound.class, () -> {
                        this.orderService.getOrderById(orderId);
                });

                verify(this.orderRepository, times(1))
                                .findById(orderId);
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

                assertDoesNotThrow(() -> {
                        this.orderService.checkStockBeforeOrder(request);
                });

                verify(this.productService, times(1))
                                .getProductById(productId);
        }

        @Test
        @DisplayName("TC2_CSBO: Kiểm tra tồn kho sản phẩm nhưng sản phẩm không tồn tại")
        void test_CheckStockBeforeOrder_ButProductNotFound() throws ProductNotFound {
                UUID productId = this.fakeDataForTest.getProductIdFake1();
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
        @DisplayName("TC3_CSBO: Kiểm tra tồn kho sản phẩm nhưng tồn kho không tồn tại")
        void test_CheckStockBeforeOrder_ButProductNotFoundInInventory() throws ProductNotFoundInInventory {
                UUID productId = this.fakeDataForTest.getProductIdFake1();
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
        @DisplayName("TC4_CSBO: Kiểm tra tồn kho sản phẩm nhưng số lượng sản phẩm không đủ")
        void test_CheckStockBeforeOrder_ButInsufficientStock() throws InsufficientStock {
                UUID productId = this.fakeDataForTest.getProductIdFake1();
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
        @DisplayName("TC1_CO: Tạo đơn thành công")
        void test_CreateOrder_Successful() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(2L)
                                                                .price(30000000L)
                                                                .build()))
                                .build();

                Order expectedOrder = Order.builder()
                                .id(UUID.randomUUID())
                                .user(this.fakeDataForTest.getUserFake1())
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .subtotal(60000000L)
                                .shippingFee(10000L)
                                .discount(0D)
                                .totalPrice(60010000D)
                                .build();

                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.userService.getUserById(userId))
                                .thenReturn(this.fakeDataForTest.getUserFake1());
                when(this.orderRepository.save(any(Order.class)))
                                .thenReturn(expectedOrder);

                Order createdOrder = this.orderService.createOrder(request);

                assertNotNull(createdOrder);
                assertEquals(expectedOrder.getId(), createdOrder.getId());
                assertEquals(expectedOrder.getShippingAddress(), createdOrder.getShippingAddress());
                assertEquals(expectedOrder.getSubtotal(), createdOrder.getSubtotal());
                assertEquals(expectedOrder.getShippingFee(), createdOrder.getShippingFee());

                verify(this.productService, times(2)).getProductById(productId);
                verify(this.userService, times(1)).getUserById(userId);
                verify(this.inventoryService, times(1)).decreaseStock(productId, 2L);
                verify(this.cartService, times(1)).clearCart(userId);
                verify(this.orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC2_CO: Sản phẩm không tồn tại")
        void test_CreateOrder_ButProductNotFound() throws ProductNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(2L)
                                                                .price(30000000L)
                                                                .build()))
                                .build();

                when(this.productService.getProductById(productId))
                                .thenThrow(new ProductNotFound(productId));

                assertThrows(ProductNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.productService, times(1)).getProductById(productId);
        }

        @Test
        @DisplayName("TC3_CO: Số lượng ≤ 0")
        void test_CreateOrder_ButQuantityLessThanOrEqualZero() throws OrderItemQuantityGreaterThanZero {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(0L)
                                                                .price(30000000L)
                                                                .build()))
                                .build();

                assertThrows(OrderItemQuantityGreaterThanZero.class, () -> {
                        this.orderService.createOrder(request);
                });
        }

        @Test
        @DisplayName("TC4_CO: Giá ≤ 0")
        void test_CreateOrder_ButPriceLessThanZero() throws OrderItemPriceGreaterThanOrEqualZero {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(2L)
                                                                .price(-1000L)
                                                                .build()))
                                .build();

                assertThrows(OrderItemPriceGreaterThanOrEqualZero.class, () -> {
                        this.orderService.createOrder(request);
                });
        }

        @Test
        @DisplayName("TC5_CO: Tạo đơn hàng nhưng tồn kho của tồn kho không tồn tại")
        void test_CreateOrder_ButProductNotFoundInInventory() throws ProductNotFoundInInventory {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                product.setInventory(null);

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(2L)
                                                                .price(30000000L)
                                                                .build()))
                                .build();

                when(this.productService.getProductById(productId))
                                .thenReturn(product);

                assertThrows(ProductNotFoundInInventory.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.productService, times(1)).getProductById(productId);
                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC6_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không tồn tại (Lỗi ProductNotFound)")
        void test_CreateOrder_ButProductNotFoundInStockCheck() throws ProductNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(2L)
                                                                .price(30000000L)
                                                                .build()))
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
        @DisplayName("TC7_CO: Tạo đơn hàng nhưng tồn kho của sản phẩm không đủ (giảm tồn kho)")
        void test_CreateOrder_ButInsufficientStock() throws InsufficientStock {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(99L) 
                                                                .price(30000000L)
                                                                .build()))
                                .build();

                when(this.productService.getProductById(productId))
                                .thenReturn(product);

                assertThrows(InsufficientStock.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.productService, times(1)).getProductById(productId);
                verify(this.orderRepository, times(0)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC8_CO: Tạo đơn hàng nhưng người dùng không tồn tại")
        void test_CreateOrder_ButUserNotFound() throws com.shopcart.exceptions.UserNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                OrderCreateRequest request = OrderCreateRequest.builder()
                                .userId(userId.toString())
                                .couponId(null)
                                .shippingAddress("Trường Đại học Sài Gọn")
                                .shippingMethod(OrderShippingMethodEnum.STANDARD)
                                .shippingFee(10000L)
                                .paymentMethod(OrderPaymentMethodEnum.COD)
                                .orderItems(List.of(
                                                OrderItemRequest.builder()
                                                                .productId(productId.toString())
                                                                .quantity(2L)
                                                                .price(30000000L)
                                                                .build()))
                                .build();

                when(this.productService.getProductById(productId))
                                .thenReturn(product);
                when(this.userService.getUserById(userId))
                                .thenThrow(new com.shopcart.exceptions.UserNotFound(userId));

                assertThrows(com.shopcart.exceptions.UserNotFound.class, () -> {
                        this.orderService.createOrder(request);
                });

                verify(this.userService, times(1)).getUserById(userId);
                verify(this.orderRepository, times(0)).save(any(Order.class));
        }
}
