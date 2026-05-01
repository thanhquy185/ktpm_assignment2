package com.shopcart.exceptions;

public class ProductNotFound extends RuntimeException {
    public ProductNotFound(String id) {
        super(String.format("Product ID %s not found", id));
    }
}
