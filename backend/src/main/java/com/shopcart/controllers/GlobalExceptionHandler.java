package com.shopcart.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shopcart.dtos.response.RestResponse;
import com.shopcart.exceptions.InvalidUUIDFormat;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleInvalidUUID(IllegalArgumentException e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_UUID_FORMAT")
                .message(new InvalidUUIDFormat().getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }
}
