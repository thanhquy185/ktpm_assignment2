package com.shopcart.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderPaymentMethodEnum {
    COD("COD", "Thanh toán khi nhận hàng"),
    BANK("BANK", "Thanh toán chuyển khoản ngân hàng");

    private final String value;
    private final String description;

    OrderPaymentMethodEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    // Giá trị lưu DB
    public String getValue() {
        return value;
    }

    // Giá trị trả về JSON
    @JsonValue
    public String getDescription() {
        return description;
    }

    // Convert DB -> Enum
    public static OrderPaymentMethodEnum fromValue(String value) {
        for (OrderPaymentMethodEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderPaymentMethodEnum: " + value);
    }
}