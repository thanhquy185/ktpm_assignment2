package com.shopcart.exceptions;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound(String id) {
        super(String.format("Order ID %s not found", id));
    }
}
