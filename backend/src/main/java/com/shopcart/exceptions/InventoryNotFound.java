package com.shopcart.exceptions;

import java.util.UUID;

public class InventoryNotFound extends RuntimeException {
    public InventoryNotFound(UUID id) {
        super(String.format("Inventory ID %s not found", id));
    }
}
