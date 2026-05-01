package com.shopcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entities.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

}
