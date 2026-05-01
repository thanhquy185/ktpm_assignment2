package com.shopcart.exceptions;

import java.util.UUID;

public class ProductNotFound extends RuntimeException {
    public ProductNotFound(UUID id) {
        super(String.format("Product ID %s not found", id));
    }
}
