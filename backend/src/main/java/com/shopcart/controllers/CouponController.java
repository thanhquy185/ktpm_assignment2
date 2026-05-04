package com.shopcart.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.Coupon;
import com.shopcart.exceptions.CouponNotFound;
import com.shopcart.exceptions.CouponNotFoundByCode;
import com.shopcart.exceptions.CouponOutOfDate;
import com.shopcart.services.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CouponController {
    private final CouponService couponService;

    @GetMapping("")
    public ResponseEntity<?> getAllCoupon() {
        List<Coupon> coupons = this.couponService.getAllCoupon();

        RestResponse<List<Coupon>> restResponse = RestResponse.<List<Coupon>>builder()
                .status(HttpStatus.OK.value())
                .message("Get all coupon is successful!")
                .data(coupons)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCouponById(@PathVariable("id") String id) {
        Coupon coupon = this.couponService.getCouponById(UUID.fromString(id));

        RestResponse<Coupon> restResponse = RestResponse.<Coupon>builder()
                .status(HttpStatus.OK.value())
                .message("Get coupon by id is successful!")
                .data(coupon)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @GetMapping("/code/{couponCode}")
    public ResponseEntity<?> getCouponByCode(@PathVariable("couponCode") String couponCode) {
        Coupon coupon = this.couponService.getCouponByCode(couponCode);

        RestResponse<Coupon> restResponse = RestResponse.<Coupon>builder()
                .status(HttpStatus.OK.value())
                .message("Get coupon by code is successful!")
                .data(coupon)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @ExceptionHandler(CouponNotFound.class)
    public ResponseEntity<?> handleCouponNotFound(CouponNotFound e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("COUPON_NOT_FOUND")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(CouponNotFoundByCode.class)
    public ResponseEntity<?> handleCouponNotFoundByCode(CouponNotFoundByCode e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("COUPON_NOT_FOUND_BY_CODE")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(CouponOutOfDate.class)
    public ResponseEntity<?> handleCouponOutOfDate(CouponOutOfDate e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("COUPON_OUT_OF_DATE")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }
}
