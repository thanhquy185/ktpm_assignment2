package com.shopcart.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.CartController;
import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.exceptions.CartNotFound;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.exceptions.UserNotFoundInCart;
import com.shopcart.services.CartService;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
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
    @DisplayName("TC1: POST /api/carts/user/{userId} - Thêm sản phẩm vào giỏ hàng thành công")
    void test_AddToCart_Successful() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        UUID productId = this.fakeDataForTest.getProductIdFake1();

        CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                .productId(productId.toString())
                .quantity(2L)
                .build();

        when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                .thenReturn(null);
        when(this.cartService.getCartByUserId(userId))
                .thenReturn(this.fakeDataForTest.getCartFake1());

        this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.message", is("Add product to cart is successful!")))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.id", notNullValue()));
    }

    @Test
    @DisplayName("TC2: POST /api/carts/user/{userId} - Missing productId → 400 Bad Request")
    void test_AddToCart_MissingProductId() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();

        CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                .productId(null)
                .quantity(2L)
                .build();

        this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("TC3: POST /api/carts/user/{userId} - Missing quantity → 400 Bad Request")
    void test_AddToCart_MissingQuantity() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        UUID productId = this.fakeDataForTest.getProductIdFake1();

        CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                .productId(productId.toString())
                .quantity(null)
                .build();

        this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("TC4: POST /api/carts/user/{userId} - Product không tồn tại → 404 Not Found")
    void test_AddToCart_ProductNotFound() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        UUID productId = this.fakeDataForTest.getProductIdFake1();

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
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("PRODUCT_NOT_FOUND")));
    }

    @Test
    @DisplayName("TC5: POST /api/carts/user/{userId} - Giỏ hàng không tồn tại → 404 Not Found")
    void test_AddToCart_CartNotFound() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        UUID productId = this.fakeDataForTest.getProductIdFake1();
        UUID cartId = UUID.randomUUID();

        CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                .productId(productId.toString())
                .quantity(2L)
                .build();

        when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                .thenThrow(new CartNotFound(cartId));

        this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("CART_NOT_FOUND")));
    }

    @Test
    @DisplayName("TC6: POST /api/carts/user/{userId} - Người dùng không có giỏ hàng → 404 Not Found")
    void test_AddToCart_UserNotFoundInCart() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        UUID productId = this.fakeDataForTest.getProductIdFake1();

        CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                .productId(productId.toString())
                .quantity(2L)
                .build();

        when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                .thenThrow(new UserNotFoundInCart(userId));

        this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("USER_NOT_FOUND_IN_CART")));
    }

    @Test
    @DisplayName("TC7: POST /api/carts/user/{userId} - Tồn kho không đủ → 400 Bad Request")
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
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("INSUFFICIENT_STOCK")));
    }

    @Test
    @DisplayName("TC8: POST /api/carts/user/{userId} - Sản phẩm không có tồn kho → 404 Not Found")
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
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("PRODUCT_NOT_FOUND_IN_INVENTORY")));
    }

    @Test
    @DisplayName("TC9: POST /api/carts/user/{userId} - Verify Response Structure")
    void test_AddToCart_VerifyResponseStructure() throws Exception {
        UUID userId = this.fakeDataForTest.getUserIdFake1();
        UUID productId = this.fakeDataForTest.getProductIdFake1();

        CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                .productId(productId.toString())
                .quantity(1L)
                .build();

        when(this.cartService.addToCart(eq(userId), any(CartItemAddToCartRequest.class)))
                .thenReturn(null);
        when(this.cartService.getCartByUserId(userId))
                .thenReturn(this.fakeDataForTest.getCartFake1());

        this.mockMvc.perform(post("/api/carts/user/{userId}", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.user", notNullValue()))
                .andExpect(jsonPath("$.data.cartItems").isArray());
    }
}
