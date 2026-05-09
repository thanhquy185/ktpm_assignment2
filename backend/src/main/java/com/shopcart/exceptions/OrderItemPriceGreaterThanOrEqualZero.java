package com.shopcart.exceptions;

public class OrderItemPriceGreaterThanOrEqualZero extends RuntimeException {
    public OrderItemPriceGreaterThanOrEqualZero() {
        super("Order item price must be greater than 0");
    }

    public OrderItemPriceGreaterThanOrEqualZero(String message) {
        super(message);
    }
}
