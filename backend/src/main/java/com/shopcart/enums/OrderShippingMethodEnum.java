package com.shopcart.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderShippingMethodEnum {
    STANDARD("STANDARD", "Tiêu chuẩn"),
    FAST("FAST", "Nhanh"),
    EXPRESS("EXPRESS", "Hoả tốc");

    private final String value;
    private final String description;

    OrderShippingMethodEnum(String value, String description) {
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
    public static OrderShippingMethodEnum fromValue(String value) {
        for (OrderShippingMethodEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderShippingMethodEnum: " + value);
    }
}