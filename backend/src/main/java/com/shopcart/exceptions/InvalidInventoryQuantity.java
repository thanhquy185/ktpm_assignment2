package com.shopcart.exceptions;

public class InvalidInventoryQuantity extends RuntimeException {
    public InvalidInventoryQuantity() {
        super("Inventory quantity must be greater than 0");
    }
}
