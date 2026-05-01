package com.shopcart.exceptions;

public class CouponNotFound extends RuntimeException {
    public CouponNotFound(String id) {
        super(String.format("Coupon ID %s not found", id));
    }
}
