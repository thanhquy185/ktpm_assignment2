package com.shopcart.exceptions;

public class OrderItemQuantityGreaterThanZero extends RuntimeException {
    public OrderItemQuantityGreaterThanZero() {
        super("Order item quantity must be greater than 0");
    }

    public OrderItemQuantityGreaterThanZero(String message) {
        super(message);
    }
}
