package com.shopcart.exceptions;

public class UserNotFoundInCart extends RuntimeException {
    public UserNotFoundInCart(String userId) {
        super(String.format("User ID %s not found in cart", userId));
    }
}
