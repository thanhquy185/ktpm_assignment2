package com.shopcart.repositories.converters;

import com.shopcart.enums.OrderStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatusEnum status) {
        return (status != null) ? status.getValue() : null;
    }

    @Override
    public OrderStatusEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? OrderStatusEnum.fromValue(dbValue) : null;
    }
}