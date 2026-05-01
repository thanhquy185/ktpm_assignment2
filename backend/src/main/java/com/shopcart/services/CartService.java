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

    public Cart getCartById(String id) {
        return this.cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFound(id));
    }

    public Cart getCartByUserId(String userId) {
        return this.cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundInCart(userId));
    }

    public Cart getOrCreateCart(String userId) {
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

    private void updateCartTotal(String cartId) {
        Cart cart = this.getCartById(cartId);

        Long newTotalQuantity = cart.getItems().stream()
                .map(CartItem::getQuantity)
                .reduce(0L, Long::sum);
        cart.setTotalQuantity(newTotalQuantity);

        Long newTotalPrice = cart.getItems().stream()
                .map(item -> item.getQuantity() * item.getPrice())
                .reduce(0L, Long::sum);
        cart.setTotalPrice(newTotalPrice);

        this.cartRepository.save(cart);
    }

    public CartItem addToCart(String userId, CartItemAddToCartRequest request) {
        Product product = this.productService.getProductById(request.getProductId());
        if (request.getQuantity() <= 0) {
            throw new CartItemQuantityGreaterThanZero();
        }
        if (request.getQuantity() > product.getInventory().getStock()) {
            throw new InsufficientStock(product.getId());
        }

        Cart cart = this.getOrCreateCart(userId);
        CartItem cartItemExists = this.cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(null);
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

        updateCartTotal(cart.getId());

        return cartItemCreated;
    }

    public CartItem updateQuantity(String userId, CartItemUpdateQuantityRequest request) {
        Product product = this.productService.getProductById(request.getProductId());
        if (request.getQuantity() <= 0) {
            throw new CartItemQuantityGreaterThanZero();
        }
        if (request.getQuantity() > product.getInventory().getStock()) {
            throw new InsufficientStock(product.getId());
        }

        Cart cart = this.getCartByUserId(userId);
        CartItem cartItemUpdated = this.cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new CartItemNotFound(cart.getId(), request.getProductId()));
        cartItemUpdated.setQuantity(request.getQuantity());
        this.cartItemRepository.save(cartItemUpdated);

        updateCartTotal(cart.getId());

        return cartItemUpdated;
    }

    public CartItem removeFromCart(String userId, CartItemRemoveFromCartRequest request) {
        Cart cart = this.getCartByUserId(userId);
        CartItem cartItemDeleted = this.cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new CartItemNotFound(cart.getId(), request.getProductId()));
        this.cartItemRepository.delete(cartItemDeleted);

        updateCartTotal(cart.getId());

        return cartItemDeleted;
    }

    public void clearCart(String userId) {
        Cart cart = this.getCartByUserId(userId);
        this.cartItemRepository.deleteByCartId(cart.getId());
    }
}
