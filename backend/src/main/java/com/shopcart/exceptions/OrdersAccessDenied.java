package com.shopcart.exceptions;

public class OrdersAccessDenied extends RuntimeException {
    public OrdersAccessDenied(String userId) {
        super(String.format("Cant not get orders by user id %s", userId));
    }
}
