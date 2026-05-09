package com.shopcart.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.FakeDataForTest;
import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.dtos.request.CartItemRemoveFromCartRequest;
import com.shopcart.dtos.request.CartItemUpdateQuantityRequest;
import com.shopcart.entities.Cart;
import com.shopcart.entities.CartItem;
import com.shopcart.entities.Product;
import com.shopcart.exceptions.CartItemNotFound;
import com.shopcart.exceptions.CartItemQuantityGreaterThanZero;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
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
        @DisplayName("TC7_ATC: Thêm sản phẩm nhưng người dùng không tồn tại")
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
        @DisplayName("TC8_ATC: Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)")
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
        @DisplayName("TC9_ATC: Thêm sản phẩm đã có trong giỏ nhưng tồn kho của sản phẩm không đủ")
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
        @DisplayName("TC7_UQ: Cập nhật sản phẩm nhưng giỏ hàng của người dùng không tồn tại")
        void test_UpdateQuantity_ButUserNotFoundInCart() throws UserNotFoundInCart {
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
                verify(this.cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("TC8_UQ: Cập nhật sản phẩm nhưng sản phẩm không tồn tại trong giỏ")
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
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                                .thenReturn(Optional.empty());

                assertThrows(CartItemNotFound.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
                verify(this.cartItemRepository, times(1))
                                .findByCartIdAndProductId(cart.getId(), UUID.fromString(request.getProductId()));
                verify(this.cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("TC1_UQ: Cập nhật sản phẩm thành công")
        void test_UpdateQuantity_Successful() {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                CartItem cartItem = this.fakeDataForTest.getCartItemFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(3L)
                                .build();

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.of(cartItem));
                when(this.cartItemRepository.save(any(CartItem.class)))
                                .thenAnswer(i -> i.getArgument(0));
                when(this.cartItemRepository.sumQuantity(cart.getId()))
                                .thenReturn(3L);
                when(this.cartItemRepository.sumPrice(cart.getId()))
                                .thenReturn(product.getPrice() * 3L);
                when(this.cartRepository.findById(cart.getId()))
                                .thenReturn(Optional.of(cart));
                when(this.cartRepository.save(any(Cart.class)))
                                .thenAnswer(i -> i.getArgument(0));

                CartItem cartItemUpdated = this.cartService.updateQuantity(userId, request);
                assertNotNull(cartItemUpdated);
                assertEquals(cartItem.getId(), cartItemUpdated.getId());
                assertEquals(product, cartItemUpdated.getProduct());
                assertEquals(3L, cartItemUpdated.getQuantity());
                assertEquals(product.getPrice(), cartItemUpdated.getPrice());

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
        @DisplayName("TC2_UQ: Cập nhật sản phẩm nhưng sản phẩm không tồn tại.")
        void test_UpdateQuantity_ProductNotFound() throws ProductNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenThrow(new ProductNotFound(UUID.fromString(request.getProductId())));

                assertThrows(ProductNotFound.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
        }

        @Test
        @DisplayName("TC3_UQ: Cập nhật sản phẩm nhưng số lượng sản phẩm bé hơn 0")
        void test_UpdateQuantity_QuantityLessThanZero() throws CartItemQuantityGreaterThanZero {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(-1L)
                                .build();

                assertThrows(CartItemQuantityGreaterThanZero.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });
        }

        @Test
        @DisplayName("TC4_UQ: Cập nhật sản phẩm nhưng số lượng sản phẩm bằng 0")
        void test_UpdateQuantity_QuantityIsZero() throws CartItemQuantityGreaterThanZero {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(0L)
                                .build();

                assertThrows(CartItemQuantityGreaterThanZero.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });
        }

        @Test
        @DisplayName("TC5_UQ: Cập nhật sản phẩm nhưng tồn kho của sản phẩm không tồn tại")
        void test_UpdateQuantity_InventoryNotFound() throws ProductNotFoundInInventory {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();

                // Set inventory to null to simulate no inventory
                product.setInventory(null);

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);

                assertThrows(ProductNotFoundInInventory.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
        }

        @Test
        @DisplayName("TC6_UQ: Cập nhật sản phẩm nhưng tồn kho của sản phẩm không đủ")
        void test_UpdateQuantity_InsufficientStock() throws InsufficientStock {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                CartItemUpdateQuantityRequest request = CartItemUpdateQuantityRequest.builder()
                                .productId(productId.toString())
                                .quantity(100L) // More than available stock
                                .build();

                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);

                assertThrows(InsufficientStock.class, () -> {
                        this.cartService.updateQuantity(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
        }

        @Test
        @DisplayName("TC1_RFC: Xóa sản phẩm thành công")
        void test_RemoveFromCart_Successful() {
                // Lấy dữ liệu giả từ FakeDataForTest
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();
                CartItem cartItem = this.fakeDataForTest.getCartItemFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Giả lập các hàm get để tìm được dữ liệu
                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.of(cartItem));

                // Giả lập dữ liệu cho hàm updateCartTotal bên trong
                when(this.cartItemRepository.sumQuantity(cart.getId())).thenReturn(0L);
                when(this.cartItemRepository.sumPrice(cart.getId())).thenReturn(0L);
                when(this.cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));

                // Chạy hàm cần test
                CartItem result = this.cartService.removeFromCart(userId, request);

                // Kiểm tra kết quả trả về có chuẩn không
                assertNotNull(result);
                assertEquals(cartItem.getId(), result.getId());

                verify(this.cartItemRepository, times(1)).delete(cartItem);
                verify(this.productService, times(1)).getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1)).findByUserId(userId);
                verify(this.cartItemRepository, times(1)).findByCartIdAndProductId(cart.getId(), product.getId());
                verify(this.cartItemRepository, times(1)).sumQuantity(cart.getId());
                verify(this.cartItemRepository, times(1)).sumPrice(cart.getId());
                verify(this.cartRepository, times(1)).findById(cart.getId());
        }

        @Test
        @DisplayName("TC2_RFC: Xóa sản phẩm nhưng sản phẩm không tồn tại")
        void test_RemoveFromCart_ProductNotFound() throws ProductNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Giả lập văng lỗi ProductNotFound ngay từ bước lấy sản phẩm
                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenThrow(new ProductNotFound(UUID.fromString(request.getProductId())));

                // Kiểm tra xem hàm removeFromCart có quăng lỗi đó ra không
                assertThrows(ProductNotFound.class, () -> {
                        this.cartService.removeFromCart(userId, request);
                });

                // Xác nhận hàm lấy sản phẩm có được gọi 1 lần
                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
        }

        @Test
        @DisplayName("TC3_RFC: Xóa sản phẩm nhưng giỏ hàng của người dùng không tồn tại")
        void test_RemoveFromCart_UserNotFoundInCart() throws UserNotFoundInCart {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Sản phẩm thì tìm thấy
                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                // Nhưng tìm giỏ hàng theo userId thì báo lỗi
                when(this.cartRepository.findByUserId(userId))
                                .thenThrow(new UserNotFoundInCart(userId));

                assertThrows(UserNotFoundInCart.class, () -> {
                        this.cartService.removeFromCart(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
        }

        @Test
        @DisplayName("TC4_RFC: Xóa sản phẩm nhưng sản phẩm không tồn tại trong giỏ")
        void test_RemoveFromCart_CartItemNotFound() throws CartItemNotFound {
                UUID userId = this.fakeDataForTest.getUserIdFake1();
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                Product product = this.fakeDataForTest.getProductFake1();
                Cart cart = this.fakeDataForTest.getCartFake1();

                CartItemRemoveFromCartRequest request = CartItemRemoveFromCartRequest.builder()
                                .productId(productId.toString())
                                .build();

                // Sản phẩm và Giỏ hàng đều tìm thấy
                when(this.productService.getProductById(UUID.fromString(request.getProductId())))
                                .thenReturn(product);
                when(this.cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                // Giả lập trả về Optional rỗng (Không tìm thấy sản phẩm trong giỏ)
                when(this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.empty());

                assertThrows(CartItemNotFound.class, () -> {
                        this.cartService.removeFromCart(userId, request);
                });

                verify(this.productService, times(1))
                                .getProductById(UUID.fromString(request.getProductId()));
                verify(this.cartRepository, times(1))
                                .findByUserId(userId);
                verify(this.cartItemRepository, times(1))
                                .findByCartIdAndProductId(cart.getId(), product.getId());
        }

        @DisplayName("TC1_ATC: Thêm sản phẩm thành công")
        @ParameterizedTest
        @ValueSource(longs = { 1L, 2L, 3L, 4L, 5L })
        // ProductFake1 có inventory là 5
        void addToCart_WhenProductIsValid_ShouldAddProductSuccessfully(Long quantity) {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                Product product = fakeDataForTest.getProductFake1();
                Cart cart = Cart.builder()
                                .id(fakeDataForTest.getCartIdFake1())
                                .totalQuantity(0L)
                                .totalPrice(0L)
                                .cartItems(List.<CartItem>of())
                                .user(fakeDataForTest.getUserFake1())
                                .build();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(product.getId().toString())
                                .quantity(quantity)
                                .build();
                when(productService.getProductById(product.getId()))
                                .thenReturn(product);
                when(cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.empty());
                when(cartItemRepository.save(any(CartItem.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(cartItemRepository.sumQuantity(cart.getId()))
                                .thenReturn(2L);
                when(cartItemRepository.sumPrice(cart.getId()))
                                .thenReturn(product.getPrice());
                when(cartRepository.findById(cart.getId()))
                                .thenReturn(Optional.of(cart));
                when(cartRepository.save(any(Cart.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                CartItem result = cartService.addToCart(userId, request);

                // Assert
                assertEquals(request.getQuantity(), result.getQuantity());
                assertEquals(product.getPrice(), result.getPrice());
                assertEquals(product, result.getProduct());
                assertEquals(cart, result.getCart());

                verify(productService)
                                .getProductById(product.getId());
                verify(cartRepository)
                                .findByUserId(userId);
                verify(cartItemRepository)
                                .findByCartIdAndProductId(
                                                cart.getId(),
                                                product.getId());
                verify(cartRepository)
                                .save(any(Cart.class));
        }

        @Test
        @DisplayName("TC2_ATC: Thêm sản phẩm thành công vào giỏ hàng, cập nhật tổng trong giỏ hàng")
        void addToCart_WhenCartHasItemsAndProductIsNotDuplicate_ShouldUpdateTotalQuantityAndPrice() {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                Cart cart = fakeDataForTest.getCartFake1();
                Product product = fakeDataForTest.getProductFake2();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(product.getId().toString())
                                .quantity(2L)
                                .build();
                CartItem cartItem = CartItem.builder()
                                .id(UUID.randomUUID())
                                .price(product.getPrice())
                                .quantity(2L)
                                .cart(cart)
                                .product(product)
                                .build();
                when(productService.getProductById(product.getId()))
                                .thenReturn(product);
                when(cartRepository.findByUserId(userId))
                                .thenReturn(Optional.of(cart));
                when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                                .thenReturn(Optional.empty());
                when(cartItemRepository.save(any(CartItem.class)))
                                .thenReturn(cartItem);
                Long oldTotalQuantity = cart.getTotalQuantity();
                Long oldTotalPrice = cart.getTotalPrice();
                // Thêm 2 product vào cart
                when(cartItemRepository.sumQuantity(cart.getId()))
                                .thenReturn(oldTotalQuantity + 2L);
                when(cartItemRepository.sumPrice(cart.getId()))
                                .thenReturn(oldTotalPrice + 2L * product.getPrice());
                when(cartRepository.findById(cart.getId()))
                                .thenReturn(Optional.of(cart));
                when(cartRepository.save(any(Cart.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

                // Act
                CartItem result = cartService.addToCart(userId, request);

                // Assert
                assertEquals(request.getQuantity(), result.getQuantity());
                assertEquals(product.getPrice(), result.getPrice());
                assertEquals(product, result.getProduct());
                assertEquals(cart, result.getCart());

                verify(productService)
                                .getProductById(product.getId());
                verify(cartRepository)
                                .findByUserId(userId);
                verify(cartItemRepository)
                                .findByCartIdAndProductId(
                                                cart.getId(),
                                                product.getId());
                verify(cartRepository)
                                .save(any(Cart.class));
                verify(cartRepository)
                                .save(cartCaptor.capture());
                Cart updatedCart = cartCaptor.getValue();

                assertEquals(oldTotalQuantity + 2L, updatedCart.getTotalQuantity());
                assertEquals(oldTotalPrice + 2L * product.getPrice(), updatedCart.getTotalPrice());
        }

        @Test
        @DisplayName("TC3_ATC: Thêm sản phẩm nhưng sản phẩm không tồn tại")
        void addToCart_WhenProductNotFound_ShouldThrowProductNotFoundException() {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                UUID productId = fakeDataForTest.getProductIdFake5();

                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(productId.toString())
                                .quantity(2L)
                                .build();
                when(productService.getProductById(productId))
                                .thenThrow(new ProductNotFound(productId));
                // Act + Assert
                assertThrows(ProductNotFound.class,
                                () -> cartService.addToCart(userId, request));

                verify(productService).getProductById(productId);
                verifyNoInteractions(cartRepository);
                verifyNoInteractions(cartItemRepository);
        }

        @DisplayName("TC4_ATC: Thêm sản phẩm nhưng số lượng sản phẩm bé hơn hoặc bằng 0")
        @ParameterizedTest
        @ValueSource(longs = { -3L, -2L, -1L, 0L })
        void addToCart_WhenQuantityLessThanOrEqualsZero_ShouldThrowCartItemQuantityGreaterThanZeroException(
                        Long quantity) {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                Product product = fakeDataForTest.getProductFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(product.getId().toString())
                                .quantity(quantity)
                                .build();
                when(productService.getProductById(product.getId()))
                                .thenReturn(product);

                // Act + Assert
                assertThrows(CartItemQuantityGreaterThanZero.class,
                                () -> cartService.addToCart(userId, request));

                verify(productService).getProductById(product.getId());
                verifyNoInteractions(cartRepository);
                verifyNoInteractions(cartItemRepository);
        }

        @Test
        @DisplayName("TC5_ATC: Thêm sản phẩm nhưng tồn kho của sản phẩm không tồn tại")
        void addToCart_WhenInventoryNotFound_ShouldThrowProductNotFoundInInventoryException() {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                Product product = fakeDataForTest.getProductFake1();
                product.setInventory(null);
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(product.getId().toString())
                                .quantity(2L)
                                .build();
                when(productService.getProductById(product.getId()))
                                .thenReturn(product);

                // Act + Assert
                assertThrows(ProductNotFoundInInventory.class,
                                () -> cartService.addToCart(userId, request));

                verify(productService).getProductById(product.getId());
                verifyNoInteractions(cartRepository);
                verifyNoInteractions(cartItemRepository);
        }

        @DisplayName("TC6_ATC: Thêm sản phẩm nhưng tồn kho của sản phẩm không đủ")
        @ParameterizedTest
        @ValueSource(longs = { 6L, 7L, 10L, 100L })
        // Tồn kho của productFake1 là 5
        void addToCart_WhenInventoryIsInsufficient_ShouldThrowInsufficientStockException(Long quantity) {
                // Arrange
                UUID userId = fakeDataForTest.getUserIdFake1();
                Product product = fakeDataForTest.getProductFake1();
                CartItemAddToCartRequest request = CartItemAddToCartRequest.builder()
                                .productId(product.getId().toString())
                                .quantity(quantity)
                                .build();
                when(productService.getProductById(product.getId()))
                                .thenReturn(product);

                // Act + Assert
                assertThrows(InsufficientStock.class,
                                () -> cartService.addToCart(userId, request));

                verify(productService).getProductById(product.getId());
                verifyNoInteractions(cartRepository);
                verifyNoInteractions(cartItemRepository);
        }

}
