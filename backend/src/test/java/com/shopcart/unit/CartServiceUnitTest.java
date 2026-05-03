package com.shopcart.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.FakeDataForTest;
import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.dtos.request.CartItemUpdateQuantityRequest;
import com.shopcart.entities.Cart;
import com.shopcart.entities.CartItem;
import com.shopcart.entities.Product;
import com.shopcart.exceptions.CartItemNotFound;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.UserNotFound;
import com.shopcart.exceptions.UserNotFoundInCart;
import com.shopcart.repositories.CartItemRepository;
import com.shopcart.repositories.CartRepository;
import com.shopcart.services.CartService;
import com.shopcart.services.ProductService;
import com.shopcart.services.UserService;

@DisplayName("Cart Service Unit Tests")
@ExtendWith(MockitoExtension.class)
public class CartServiceUnitTest {
        @Mock
        private UserService userService;
        @Mock
        private ProductService productService;
        @Mock
        private CartRepository cartRepository;
        @Mock
        private CartItemRepository cartItemRepository;
        @InjectMocks
        private CartService cartService;

        private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

        @Test
        @DisplayName("TC6_ATC: Thêm sản phẩm nhưng người dùng không tồn tại")
        void test_AddToCart_ButUserNotFound() throws UserNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.ofNullable(null));
                when(this.userService.getUserById(userId))
                                .thenThrow(new UserNotFound(userId));

                assertThrows(UserNotFound.class, () -> {
                        this.cartService.addToCart(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
                verify(this.userService, times(1))
                                .getUserById(userId);
        }

        @Test
        @DisplayName("TC7_ATC: Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)")
        void test_AddToCart_WithProductExists() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                CartItem cartItem = this.fakeDataForTest.getCartItemFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();
                Long newQuantity = cartItem.getQuantity() + 2L;

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.of(cartItem));
                when(this.cartItemRepository.save(any(CartItem.class)))
                                .thenAnswer(i -> i.getArgument(0));
                when(this.cartItemRepository.sumQuantity(cart.getId()))
                                .thenReturn(newQuantity);
                when(this.cartItemRepository.sumPrice(cart.getId()))
                                .thenReturn(product.getPrice() * newQuantity);
                when(this.cartRepository.findById(cart.getId()))
                                .thenReturn(Optional.of(cart));
                when(this.cartRepository.save(any(Cart.class)))
                                .thenAnswer(i -> i.getArgument(0));

                CartItem cartItemCreated = this.cartService.addToCart(userId, request);
                assertNotNull(cartItemCreated);
                assertEquals(cartItem.getId(), cartItemCreated.getId());
                // assertEquals(cart, cartItemCreated.getCart());
                assertEquals(product, cartItemCreated.getProduct());
                assertEquals(newQuantity, cartItemCreated.getQuantity());
                assertEquals(product.getPrice(), cartItemCreated.getPrice());

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
                verify(this.cartItemRepository, times(1))
                                .findByCartIdAndProductId(cart.getId(), product.getId());
                verify(this.cartItemRepository, times(1))
                                .save(any(CartItem.class));
                verify(this.cartItemRepository, times(1))
                                .sumQuantity(cart.getId());
                verify(this.cartItemRepository, times(1))
                                .sumPrice(cart.getId());
                verify(this.cartRepository, times(1))
                                .findById(cart.getId());
                verify(this.cartRepository, times(1))
                                .save(any(Cart.class));
        }

        @Test
        @DisplayName("TC8_ATC: Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ")
        void test_AddToCart_WithProductExistsButInsufficientStock() throws InsufficientStock {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                CartItem cartItem = this.fakeDataForTest.getCartItemFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(3L)
                                .build();
                // // 6 > 5 (current product stock)
                // Long newQuantity = cartItem.getQuantity() + 3L;

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.of(cartItem));

                assertThrows(InsufficientStock.class, () -> {
                        this.cartService.addToCart(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
                verify(this.cartItemRepository, times(1))
                                .findByCartIdAndProductId(cart.getId(), product.getId());
        }

        @Test
        @DisplayName("TC6_UQ: Cập nhật sản phẩm nhưng giỏ hàng của người dùng không tồn tại")
        void test_UpdateQuantity_ButUserNotFound() throws UserNotFoundInCart {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenThrow(new UserNotFoundInCart(userId));

                assertThrows(UserNotFoundInCart.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
        }

        @Test
        @DisplayName("TC7_UQ: Cập nhật sản phẩm nhưng sản phẩm không tồn tại trong giỏ")
        void test_UpdateQuantity_ButProductNotExistsInCart() throws CartItemNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenThrow(new CartItemNotFound(cart.getId(), product.getId()));

                assertThrows(CartItemNotFound.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
                verify(this.cartItemRepository, times(1))
                                .findByCartIdAndProductId(cart.getId(), product.getId());
        }
}
