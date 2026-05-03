package com.shopcart.exceptions;

public class InventoryItemQuantityGreaterThanZero extends RuntimeException {
    public InventoryItemQuantityGreaterThanZero() {
        super("Inventory item quantity must be greater than 0");
    }
}
