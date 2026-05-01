package com.shopcart.exceptions;

public class ProductNotFoundInInventory extends RuntimeException {
    public ProductNotFoundInInventory(String productId) {
        super(String.format("Product ID %s not found in inventory", productId));
    }
}
