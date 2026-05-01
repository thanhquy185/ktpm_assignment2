package com.shopcart.exceptions;

import java.util.UUID;

public class UserNotFoundInCart extends RuntimeException {
    public UserNotFoundInCart(UUID userId) {
        super(String.format("User ID %s not found in cart", userId));
    }
}
