package com.shopcart.integration;

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
import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.entities.Cart;
import com.shopcart.entities.CartItem;
import com.shopcart.entities.Product;
import com.shopcart.exceptions.CartItemQuantityGreaterThanZero;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.exceptions.UserNotFound;
import com.shopcart.services.CartService;

import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Cart Controller Integration Tests")
public class CartControllerIntegrationTest {
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
        @DisplayName("TC1_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm thành công")
        void test_AddToCart_Successful() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();
                CartItem response = CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(2L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenReturn(response);
                when(this.cartService.getCartByUserId(eq(userId)))
                                .thenReturn(cart);

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isNotEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                                .andExpect(jsonPath("$.message").value("Add product to cart is successful!"))
                                .andExpect(jsonPath("$.error").value(nullValue()))
                                .andExpect(jsonPath("$.data.id").value(cart.getId().toString()));
        }

        @Test
        @DisplayName("TC2_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm nhưng sản phẩm không tồn tại")
        void test_AddToCart_ProductNotFound() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = UUID.randomUUID();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new ProductNotFound(productId));

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                                .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value(new ProductNotFound(productId).getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }

        @Test
        @DisplayName("TC3_ATC: POST  /api/carts/user/{userId} - Thêm sản phẩm nhưng số lượng sản phẩm bé hơn 0")
        void test_AddToCart_QuantityLessThanZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(-1L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new CartItemQuantityGreaterThanZero());

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                                .andExpect(jsonPath("$.error").value("CART_ITEM_QUANTITY_GREATER_THAN_ZERO"))
                                .andExpect(jsonPath("$.message")
                                                .value(new CartItemQuantityGreaterThanZero().getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }

        @Test
        @DisplayName("TC4_ATC: POST  /api/carts/user/{userId} - Thêm sản phẩm nhưng số lượng sản phẩm bằng 0")
        void test_AddToCart_QuantityEqualZero() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(0L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new CartItemQuantityGreaterThanZero());

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                                .andExpect(jsonPath("$.error").value("CART_ITEM_QUANTITY_GREATER_THAN_ZERO"))
                                .andExpect(jsonPath("$.message")
                                                .value(new CartItemQuantityGreaterThanZero().getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }

        @Test
        @DisplayName("TC5_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại")
        void test_AddToCart_ProductNotFoundInInventory() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new ProductNotFoundInInventory(productId));

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                                .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND_IN_INVENTORY"))
                                .andExpect(jsonPath("$.message")
                                                .value(new ProductNotFoundInInventory(productId).getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }

        @Test
        @DisplayName("TC6_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ")
        void test_AddToCart_InsufficientStock() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(100L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new InsufficientStock(productId));

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                                .andExpect(jsonPath("$.error").value("INSUFFICIENT_STOCK"))
                                .andExpect(jsonPath("$.message").value(new InsufficientStock(productId).getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }

        @Test
        @DisplayName("TC7_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm nhưng người dùng không tồn tại")
        void test_AddToCart_UserNotFound() throws Exception {
                UUID userId = UUID.randomUUID();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new UserNotFound(userId));

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value(new UserNotFound(userId).getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }

        @Test
        @DisplayName("TC8_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)")
        void test_AddToCart_ProductExistedInCartSuccessful() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();
                CartItem response = CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(3L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenReturn(response);
                when(this.cartService.getCartByUserId(eq(userId)))
                                .thenReturn(cart);

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isNotEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                                .andExpect(jsonPath("$.message").value("Add product to cart is successful!"))
                                .andExpect(jsonPath("$.error").value(nullValue()))
                                .andExpect(jsonPath("$.data.id").value(cart.getId().toString()))
                                .andExpect(jsonPath("$.data.cartItems[0].product.id").value(productId.toString()))
                                .andExpect(jsonPath("$.data.cartItems[0].quantity").value(3L));
        }

        @Test
        @DisplayName("TC9_ATC: POST /api/carts/user/{userId} - Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ")
        void test_AddToCart_ProductExistedInCartButInsufficientStock() throws Exception {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                                .thenThrow(new InsufficientStock(productId));

                this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").isNumber())
                                .andExpect(jsonPath("$.error").isString())
                                .andExpect(jsonPath("$.message").isString())
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                                .andExpect(jsonPath("$.error").value("INSUFFICIENT_STOCK"))
                                .andExpect(jsonPath("$.message").value(new InsufficientStock(productId).getMessage()))
                                .andExpect(jsonPath("$.data").value(nullValue()));
        }
}
