package com.shopcart.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CouponTypeEnum {
    FIXED("FIXED", "Giảm tiền cố định"),
    PERCENT("PERCENT", "Giảm theo phần trăm");

    private final String value;
    private final String description;

    CouponTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    // Giá trị dùng để lưu DB
    public String getValue() {
        return value;
    }

    // Giá trị trả ra JSON
    @JsonValue
    public String getDescription() {
        return description;
    }

    // Convert từ DB -> Enum
    public static CouponTypeEnum fromValue(String value) {
        for (CouponTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid DiscountTypeEnum: " + value);
    }
}
