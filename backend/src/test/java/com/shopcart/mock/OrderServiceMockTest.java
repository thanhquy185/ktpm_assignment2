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
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.entities.Product;
import com.shopcart.repositories.OrderRepository;
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
}
