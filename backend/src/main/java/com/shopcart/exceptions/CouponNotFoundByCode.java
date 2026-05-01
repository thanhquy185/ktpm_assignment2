package com.shopcart.exceptions;

public class CouponNotFoundByCode extends RuntimeException {
    public CouponNotFoundByCode(String code) {
        super(String.format("Coupon not found by code %s", code));
    }
}
