package com.shopcart.exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String id) {
        super(String.format("User ID %s not found", id));
    }
}
