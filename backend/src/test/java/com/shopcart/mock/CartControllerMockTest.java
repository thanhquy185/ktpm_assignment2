package com.shopcart.mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.CartController;
import com.shopcart.dtos.request.CartItemUpdateQuantityRequest;
import com.shopcart.entities.Cart;
import com.shopcart.exceptions.CartItemNotFound;
import com.shopcart.exceptions.CartItemQuantityGreaterThanZero;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.services.CartService;
import com.shopcart.dtos.request.CartItemRemoveFromCartRequest;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Cart Controller Mock Tests")
public class CartControllerMockTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private CartService cartService;

        private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

        @Test
        @DisplayName("GET /api/carts - Lấy ra danh sách tất cả giỏ hàng")
        void test_GetAllCart() throws Exception {
                List<Cart> carts = this.fakeDataForTest.getCartsFake();

                when(this.cartService.getAllCart())
                                .thenReturn(carts);

                mockMvc.perform(get("/api/carts"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                                .andExpect(jsonPath("$.message").value("Get all cart is successful!"))
                                .andExpect(jsonPath("$.data.length()").value(carts.size()))
                                .andExpect(jsonPath("$.data[0].id").value(carts.get(0).getId().toString()))
                                .andExpect(jsonPath("$.data[1].id").value(carts.get(1).getId().toString()));

                verify(this.cartService, times(1))
                                .getAllCart();
        }

        @Test
        @DisplayName("GET /api/carts/{id} - Lấy ra giỏ hàng theo mã giỏ hàng")
        void test_GetCartById() throws Exception {
                UUID cartId = this.fakeDataForTest.getCartIdFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();

                when(this.cartService.getCartById(cartId))
                                .thenReturn(cart);

                mockMvc.perform(get("/api/carts/{id}", cartId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.data").isNotEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                                .andExpect(jsonPath("$.message").value("Get cart by id is successful!"))
                                .andExpect(jsonPath("$.data.id").value(cart.getId().toString()));

                verify(this.cartService, times(1))
                                .getCartById(cartId);
        }

        @Test
        @DisplayName("GET /api/carts/user/{userId} - Lấy ra giỏ hàng theo mã người dùng")
        void test_GetCartByUserId() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();

                when(this.cartService.getCartByUserId(userId))
                                .thenReturn(cart);

                mockMvc.perform(get("/api/carts/user/{userId}", userId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.data").isNotEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                                .andExpect(jsonPath("$.message").value("Get cart by user id is successful!"))
                                .andExpect(jsonPath("$.data.id").value(cart.getId().toString()));

                verify(this.cartService, times(1))
                                .getCartByUserId(userId);
        }

        @Test
        @DisplayName("TC1: PUT /api/carts/user/{userId} - Cập nhật số lượng sản phẩm trong giỏ hàng thành công")
        void test_UpdateQuantity_Successful() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(5L)
                                .build();

                when(this.cartService.updateQuantity(eq(userId), any(CartItemUpdateQuantityRequest.class)))
                                .thenReturn(null);
                when(this.cartService.getCartByUserId(userId))
                                .thenReturn(this.fakeDataForTest.getCartFake1());

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.message", is("Update product quantity in cart is successful!")))
                                .andExpect(jsonPath("$.data", notNullValue()))
                                .andExpect(jsonPath("$.data.id", notNullValue()));

                verify(this.cartService, times(1))
                                .updateQuantity(eq(userId), any(CartItemUpdateQuantityRequest.class));
                verify(this.cartService, times(1))
                                .getCartByUserId(userId);
        }

        @Test
        @DisplayName("TC2: PUT /api/carts/user/{userId} - Missing productId → 400 Bad Request")
        void test_UpdateQuantity_MissingProductId() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(null)
                                .quantity(5L)
                                .build();

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)));
        }

        @Test
        @DisplayName("TC3: PUT /api/carts/user/{userId} - Missing quantity → 400 Bad Request")
        void test_UpdateQuantity_MissingQuantity() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(null)
                                .build();

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)));
        }

        @Test
        @DisplayName("TC4: PUT /api/carts/user/{userId} - Sản phẩm không tồn tại trong giỏ → 404 Not Found")
        void test_UpdateQuantity_CartItemNotFound() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                UUID cartId = this.fakeDataForTest.getCartIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(5L)
                                .build();

                when(this.cartService.updateQuantity(eq(userId), any(CartItemUpdateQuantityRequest.class)))
                                .thenThrow(new CartItemNotFound(cartId, productId));

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status", is(404)))
                                .andExpect(jsonPath("$.error", is("CART_ITEM_NOT_FOUND")));
        }

        @Test
        @DisplayName("TC5: PUT /api/carts/user/{userId} - Số lượng không hợp lệ → 400 Bad Request")
        void test_UpdateQuantity_QuantityGreaterThanZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(0L)
                                .build();

                when(this.cartService.updateQuantity(eq(userId), any(CartItemUpdateQuantityRequest.class)))
                                .thenThrow(new CartItemQuantityGreaterThanZero());

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)))
                                .andExpect(jsonPath("$.error", is("CART_ITEM_QUANTITY_GREATER_THAN_ZERO")));
        }

        @Test
        @DisplayName("TC6: PUT /api/carts/user/{userId} - Tồn kho không đủ → 400 Bad Request")
        void test_UpdateQuantity_InsufficientStock() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(100L)
                                .build();

                when(this.cartService.updateQuantity(eq(userId), any(CartItemUpdateQuantityRequest.class)))
                                .thenThrow(new InsufficientStock(productId));

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status", is(400)))
                                .andExpect(jsonPath("$.error", is("INSUFFICIENT_STOCK")));
        }

        @Test
        @DisplayName("TC7:PUT /api/carts/user/{userId} - Verify Response Structure")
        void test_UpdateQuantity_VerifyResponseStructure() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(3L)
                                .build();

                when(this.cartService.updateQuantity(eq(userId), any(CartItemUpdateQuantityRequest.class)))
                                .thenReturn(null);
                when(this.cartService.getCartByUserId(userId))
                                .thenReturn(this.fakeDataForTest.getCartFake1());

                mockMvc.perform(put("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", notNullValue()))
                                .andExpect(jsonPath("$.message", notNullValue()))
                                .andExpect(jsonPath("$.data", notNullValue()))
                                .andExpect(jsonPath("$.data.id", notNullValue()))
                                .andExpect(jsonPath("$.data.user", notNullValue()))
                                .andExpect(jsonPath("$.data.cartItems").isArray());
        }

        @Test
        @DisplayName("TC1_RFC: DELETE /api/carts/user/{userId} - Xóa sản phẩm khỏi giỏ thành công (200 OK)")
        void test_RemoveFromCart_Successful() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Giả lập Service xóa thành công (trả về null vì Controller không dùng giá trị trả về của removeFromCart)
                when(this.cartService.removeFromCart(eq(userId), any(CartItemRemoveFromCartRequest.class)))
                                .thenReturn(null);
                // Giả lập Service lấy giỏ hàng sau khi xóa
                when(this.cartService.getCartByUserId(userId))
                                .thenReturn(this.fakeDataForTest.getCartFake1());

                // Đóng giả Frontend gọi API DELETE
                mockMvc.perform(delete("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk()) // Kỳ vọng HTTP 200
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.message", is("Remove product from cart is successful!")))
                                .andExpect(jsonPath("$.data", notNullValue()));

                // Xác minh các hàm Service đã được Controller gọi
                verify(this.cartService, times(1))
                                .removeFromCart(eq(userId), any(CartItemRemoveFromCartRequest.class));
                verify(this.cartService, times(1))
                                .getCartByUserId(userId);
        }

        @Test
        @DisplayName("TC2_RFC: DELETE /api/carts/user/{userId} - Thiếu productId trong Request (400 Bad Request)")
        void test_RemoveFromCart_MissingProductId() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();

                // Tạo request nhưng cố tình để productId là null
                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(null)
                                .build();

                mockMvc.perform(delete("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest()) // Lớp Validation (@Valid) sẽ chặn lại và trả 400
                                .andExpect(jsonPath("$.status", is(400)));
        }

        @Test
        @DisplayName("TC3_RFC: DELETE /api/carts/user/{userId} - Sản phẩm không tồn tại (404 Not Found)")
        void test_RemoveFromCart_ProductNotFound() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Giả lập Service văng lỗi ProductNotFound
                when(this.cartService.removeFromCart(eq(userId), any(CartItemRemoveFromCartRequest.class)))
                                .thenThrow(new com.shopcart.exceptions.ProductNotFound(productId));

                mockMvc.perform(delete("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound()) // ExceptionHandler bắt lỗi và trả về 404
                                .andExpect(jsonPath("$.status", is(404)))
                                .andExpect(jsonPath("$.error", is("PRODUCT_NOT_FOUND")));
        }

        @Test
        @DisplayName("TC4_RFC: DELETE /api/carts/user/{userId} - Giỏ hàng của user không tồn tại (404 Not Found)")
        void test_RemoveFromCart_UserNotFoundInCart() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Giả lập Service văng lỗi UserNotFoundInCart
                when(this.cartService.removeFromCart(eq(userId), any(CartItemRemoveFromCartRequest.class)))
                                .thenThrow(new com.shopcart.exceptions.UserNotFoundInCart(userId));

                mockMvc.perform(delete("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status", is(404)))
                                .andExpect(jsonPath("$.error", is("USER_NOT_FOUND_IN_CART")));
        }

        @Test
        @DisplayName("TC5_RFC: DELETE /api/carts/user/{userId} - Sản phẩm không có trong giỏ (404 Not Found)")
        void test_RemoveFromCart_CartItemNotFound() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                UUID cartId = this.fakeDataForTest.getCartIdFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Giả lập Service văng lỗi CartItemNotFound
                when(this.cartService.removeFromCart(eq(userId), any(CartItemRemoveFromCartRequest.class)))
                                .thenThrow(new CartItemNotFound(cartId, productId));

                mockMvc.perform(delete("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status", is(404)))
                                .andExpect(jsonPath("$.error", is("CART_ITEM_NOT_FOUND")));
        }

        @Test
        @DisplayName("TC6_RFC: DELETE /api/carts/user/{userId} - Kiểm tra cấu trúc JSON trả về hợp lệ")
        void test_RemoveFromCart_VerifyResponseStructure() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                when(this.cartService.removeFromCart(eq(userId), any(CartItemRemoveFromCartRequest.class)))
                                .thenReturn(null);
                when(this.cartService.getCartByUserId(userId))
                                .thenReturn(this.fakeDataForTest.getCartFake1());

                mockMvc.perform(delete("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", notNullValue()))
                                .andExpect(jsonPath("$.message", notNullValue()))
                                .andExpect(jsonPath("$.data", notNullValue()))
                                .andExpect(jsonPath("$.data.id", notNullValue()))
                                .andExpect(jsonPath("$.data.user", notNullValue()))
                                .andExpect(jsonPath("$.data.cartItems").isArray());
        }

}
