package com.shopcart.exceptions;

public class CartItemQuantityGreaterThanZero extends RuntimeException {
    public CartItemQuantityGreaterThanZero() {
        super("Cart item quantity must be greater than 0");
    }

    public CartItemQuantityGreaterThanZero(String message) {
        super(message);
    }
}
