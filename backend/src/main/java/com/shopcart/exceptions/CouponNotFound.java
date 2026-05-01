package com.shopcart.exceptions;

import java.util.UUID;

public class CouponNotFound extends RuntimeException {
    public CouponNotFound(UUID id) {
        super(String.format("Coupon ID %s not found", id));
    }
}
