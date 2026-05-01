package com.shopcart.exceptions;

public class UserNotFoundByUsername extends RuntimeException {
    public UserNotFoundByUsername(String username) {
        super(String.format("User username %s not found", username));
    }
}
