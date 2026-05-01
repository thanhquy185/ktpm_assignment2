package com.shopcart.exceptions;

public class OrderAlreadyCancelled extends RuntimeException {
    public OrderAlreadyCancelled(String id) {
        super(String.format("Order ID %s already cancelled", id));
    }
}
