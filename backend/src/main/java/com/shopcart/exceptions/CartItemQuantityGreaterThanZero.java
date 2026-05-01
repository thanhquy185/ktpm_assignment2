package com.shopcart.exceptions;

public class CartItemQuantityGreaterThanZero extends RuntimeException {
    public CartItemQuantityGreaterThanZero() {
        super("Quantity must be greater than 0");
    }
}
