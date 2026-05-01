package com.shopcart.services;

import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.dtos.request.CartItemRemoveFromCartRequest;
import com.shopcart.dtos.request.CartItemUpdateQuantityRequest;
import com.shopcart.entities.Cart;
import com.shopcart.entities.CartItem;
import com.shopcart.entities.Product;
import com.shopcart.exceptions.CartItemNotFound;
import com.shopcart.exceptions.CartItemQuantityGreaterThanZero;
import com.shopcart.exceptions.CartNotFound;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.UserNotFoundInCart;
import com.shopcart.repositories.CartItemRepository;
import com.shopcart.repositories.CartRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final UserService userService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public List<Cart> getAllCart() {
        return this.cartRepository.findAll();
    }

    public Cart getCartById(UUID id) {
        return this.cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFound(id));
    }

    public Cart getCartByUserId(UUID userId) {
        return this.cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundInCart(userId));
    }

    private Cart getOrCreateCart(UUID userId) {
        Cart cart = this.cartRepository.findByUserId(userId).orElseGet(() -> {
            return Cart.builder()
                    .user(this.userService.getUserById(userId))
                    .totalQuantity(0L)
                    .totalPrice(0L)
                    .build();
        });
        cart = this.cartRepository.save(cart);

        return cart;
    }

    private void updateCartTotal(UUID cartId) {
        Long totalQuantity = this.cartItemRepository.sumQuantity(cartId);
        Long totalPrice = this.cartItemRepository.sumPrice(cartId);

        Cart cart = getCartById(cartId);
        cart.setTotalQuantity(totalQuantity != null ? totalQuantity : 0L);
        cart.setTotalPrice(totalPrice != null ? totalPrice : 0L);

        this.cartRepository.save(cart);
    }

    public CartItem addToCart(UUID userId, CartItemAddToCartRequest request) {
        Product product = this.productService.getProductById(UUID.fromString(request.getProductId()));
        if (request.getQuantity() <= 0) {
            throw new CartItemQuantityGreaterThanZero();
        }
        if (request.getQuantity() > product.getInventory().getStock()) {
            throw new InsufficientStock(product.getId());
        }

        Cart cart = this.getOrCreateCart(userId);
        CartItem cartItemExists = this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);
        CartItem cartItemCreated;
        if (cartItemExists != null) {
            Long newQuantity = cartItemExists.getQuantity() + request.getQuantity();
            if (newQuantity > product.getInventory().getStock()) {
                throw new InsufficientStock(product.getId());
            }

            cartItemExists.setQuantity(newQuantity);
            cartItemCreated = this.cartItemRepository.save(cartItemExists);
        } else {
            cartItemCreated = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();
            cartItemCreated = this.cartItemRepository.save(cartItemCreated);
        }

        this.updateCartTotal(cart.getId());

        return cartItemCreated;
    }

    public CartItem updateQuantity(UUID userId, CartItemUpdateQuantityRequest request) {
        Product product = this.productService.getProductById(UUID.fromString(request.getProductId()));
        if (request.getQuantity() <= 0) {
            throw new CartItemQuantityGreaterThanZero();
        }
        if (request.getQuantity() > product.getInventory().getStock()) {
            throw new InsufficientStock(product.getId());
        }

        Cart cart = this.getCartByUserId(userId);
        CartItem cartItemUpdated = this.cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new CartItemNotFound(cart.getId(), product.getId()));
        cartItemUpdated.setQuantity(request.getQuantity());
        this.cartItemRepository.save(cartItemUpdated);

        this.updateCartTotal(cart.getId());

        return cartItemUpdated;
    }

    public CartItem removeFromCart(UUID userId, CartItemRemoveFromCartRequest request) {
        UUID productId = UUID.fromString(request.getProductId());
        Cart cart = this.getCartByUserId(userId);
        CartItem cartItemDeleted = this.cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartItemNotFound(cart.getId(), productId));
        this.cartItemRepository.delete(cartItemDeleted);

        this.updateCartTotal(cart.getId());

        return cartItemDeleted;
    }

    public void clearCart(UUID userId) {
        Cart cart = this.getCartByUserId(userId);
        this.cartItemRepository.deleteByCartId(cart.getId());

        this.updateCartTotal(userId);
    }
}
