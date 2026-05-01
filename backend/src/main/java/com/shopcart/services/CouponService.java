package com.shopcart.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopcart.entities.Coupon;
import com.shopcart.enums.CouponTypeEnum;
import com.shopcart.exceptions.CouponNotFound;
import com.shopcart.repositories.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    public List<Coupon> getAllCoupon() {
        return this.couponRepository.findAll();
    }

    public Coupon getCouponById(String id) {
        return this.couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFound(id));
    }

    public double calculateDiscount(String type, Long value, Long subtotal) {
        double result = 0;
        if (type.equals(CouponTypeEnum.FIXED.getValue())) {
            result = value;
        } else {
            result = 1.0 * subtotal * value / 100;
        }

        return result;
    }
}
