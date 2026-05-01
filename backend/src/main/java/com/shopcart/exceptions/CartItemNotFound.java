package com.shopcart.exceptions;

public class CartItemNotFound extends RuntimeException {
    public CartItemNotFound(String cartId, String productId) {
        super(String.format("Cart item not found by cart ID %s and product ID %s", cartId, productId));
    }
}
