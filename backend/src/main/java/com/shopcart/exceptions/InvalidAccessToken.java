package com.shopcart.exceptions;

public class InvalidAccessToken extends RuntimeException {
    public InvalidAccessToken() {
        super("Invalid access token!");
    }

}
