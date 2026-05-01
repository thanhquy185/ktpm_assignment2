package com.shopcart.exceptions;

import java.util.UUID;

public class OrderAlreadyCancelled extends RuntimeException {
    public OrderAlreadyCancelled(UUID id) {
        super(String.format("Order ID %s already cancelled", id));
    }
}
