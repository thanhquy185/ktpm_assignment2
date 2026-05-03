package com.shopcart.exceptions;

public class OrderItemPriceGreaterThanOrEqualZero extends RuntimeException {
    public OrderItemPriceGreaterThanOrEqualZero() {
        super("Order item quantity must be greater than or equal 0");
    }
}
