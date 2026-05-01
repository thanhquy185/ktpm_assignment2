package com.shopcart.exceptions;

import java.util.UUID;

public class CartNotFound extends RuntimeException {
    public CartNotFound(UUID id) {
        super(String.format("Cart ID %s not found", id));
    }
}
