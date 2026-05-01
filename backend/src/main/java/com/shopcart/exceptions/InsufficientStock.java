package com.shopcart.exceptions;

public class InsufficientStock extends RuntimeException {
    public InsufficientStock(String productId) {
        super(String.format("Insufficient stock for product ID %s", productId));
    }
}
