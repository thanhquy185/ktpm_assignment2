package com.shopcart.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductStatusEnum {
    ACTIVE("ACTIVE", "Đang bán"),
    INACTIVE("INACTIVE", "Ngừng bán");

    private final String value;
    private final String description;

    ProductStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    // Lưu DB
    public String getValue() {
        return value;
    }

    // Trả JSON
    @JsonValue
    public String getDescription() {
        return description;
    }

    // DB -> Enum
    public static ProductStatusEnum fromValue(String value) {
        for (ProductStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ProductStatusEnum: " + value);
    }
}