package com.shopcart.exceptions;

import java.util.UUID;

public class UserNotFound extends RuntimeException {
    public UserNotFound(UUID id) {
        super(String.format("User ID %s not found", id));
    }
}
