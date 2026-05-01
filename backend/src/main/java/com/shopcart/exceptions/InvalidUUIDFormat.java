package com.shopcart.exceptions;

public class InvalidUUIDFormat extends RuntimeException {
    public InvalidUUIDFormat() {
        super("Invalid UUID format!");
    }
}
