package com.shopcart.exceptions;

import java.util.UUID;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound(UUID id) {
        super(String.format("Order ID %s not found", id));
    }
}
