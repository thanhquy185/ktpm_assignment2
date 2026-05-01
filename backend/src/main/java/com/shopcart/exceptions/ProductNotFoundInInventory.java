package com.shopcart.exceptions;

import java.util.UUID;

public class ProductNotFoundInInventory extends RuntimeException {
    public ProductNotFoundInInventory(UUID productId) {
        super(String.format("Product ID %s not found in inventory", productId));
    }
}
