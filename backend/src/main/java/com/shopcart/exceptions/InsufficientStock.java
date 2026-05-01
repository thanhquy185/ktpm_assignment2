package com.shopcart.exceptions;

import java.util.UUID;

public class InsufficientStock extends RuntimeException {
    public InsufficientStock(UUID productId) {
        super(String.format("Insufficient stock for product ID %s", productId));
    }
}
