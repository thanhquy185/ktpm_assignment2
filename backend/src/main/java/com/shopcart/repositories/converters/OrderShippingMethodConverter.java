package com.shopcart.repositories.converters;

import com.shopcart.enums.OrderShippingMethodEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderShippingMethodConverter implements AttributeConverter<OrderShippingMethodEnum, String> {

    @Override
    public String convertToDatabaseColumn(OrderShippingMethodEnum status) {
        return (status != null) ? status.getValue() : null;
    }

    @Override
    public OrderShippingMethodEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? OrderShippingMethodEnum.fromValue(dbValue) : null;
    }
}