package com.shopcart.exceptions;

public class CouponOutOfDate extends RuntimeException {
    public CouponOutOfDate(String code) {
        super(String.format("Coupon code %s is out of date", code));
    }
}
