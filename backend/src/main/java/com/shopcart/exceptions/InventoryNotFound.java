package com.shopcart.exceptions;

public class InventoryNotFound extends RuntimeException {
    public InventoryNotFound(String id) {
        super(String.format("Inventory ID %s not found", id));
    }
}
