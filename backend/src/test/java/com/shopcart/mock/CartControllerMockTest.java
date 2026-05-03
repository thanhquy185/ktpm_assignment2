package com.shopcart.mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.CartController;
import com.shopcart.entities.Cart;
import com.shopcart.services.CartService;

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
}
