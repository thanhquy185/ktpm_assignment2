package com.shopcart.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopcart.enums.CouponTypeEnum;
import com.shopcart.repositories.converters.CouponTypeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate dateStart;

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate dateEnd;

    @Convert(converter = CouponTypeConverter.class)
    @Column(nullable = false)
    private CouponTypeEnum type;

    @Column(nullable = false)
    private Long discount;

    @OneToMany(mappedBy = "coupon")
    @JsonIgnore
    private List<Order> orders;
}