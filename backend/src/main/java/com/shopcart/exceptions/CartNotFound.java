package com.shopcart.exceptions;

public class CartNotFound extends RuntimeException {
    public CartNotFound(String id) {
        super(String.format("Cart ID %s not found", id));
    }
}
