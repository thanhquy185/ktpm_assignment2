package com.shopcart.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopcart.entities.Coupon;
import com.shopcart.enums.CouponTypeEnum;
import com.shopcart.exceptions.CouponNotFound;
import com.shopcart.exceptions.CouponNotFoundByCode;
import com.shopcart.exceptions.CouponOutOfDate;
import com.shopcart.repositories.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    public List<Coupon> getAllCoupon() {
        return this.couponRepository.findAll();
    }

    public Coupon getCouponById(UUID id) {
        return this.couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFound(id));
    }

    public Coupon getCouponByCode(String code) {
        return this.couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponNotFoundByCode(code));
    }

    public void checkOutOfDate(LocalDate currentDate, String code, LocalDate dateStart, LocalDate dateEnd) {
        if (currentDate.isBefore(dateStart) || currentDate.isAfter(dateEnd)) {
            throw new CouponOutOfDate(code);
        }
    }

    public double calculateDiscount(String type, Long value, Long subtotal) {
        double discount = 0;
        if (type.equals(CouponTypeEnum.FIXED.getValue())) {
            discount = value;
        } else {
            discount = 1.0 * subtotal * value / 100;
        }

        return discount;
    }
}
