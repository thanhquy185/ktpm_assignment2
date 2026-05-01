package com.shopcart.exceptions;

import java.util.UUID;

public class CartItemNotFound extends RuntimeException {
    public CartItemNotFound(UUID cartId, UUID productId) {
        super(String.format("Cart item not found by cart ID %s and product ID %s", cartId, productId));
    }
}
