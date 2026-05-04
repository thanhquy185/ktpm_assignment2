package com.shopcart.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatusEnum {
    PENDING("PENDING", "Chờ xác nhận"),
    CANCELLED("CANCELLED", "Đã huỷ đơn"),
    CONFIRMED("CONFIRMED", "Đã xác nhận"),
    DELIVERED("DELIVERED", "Đã giao hàng");

    private final String value;
    private final String description;

    OrderStatusEnum(String value, String description) {
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
    public static OrderStatusEnum fromValue(String value) {
        for (OrderStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatusEnum: " + value);
    }
}