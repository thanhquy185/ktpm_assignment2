package com.shopcart.repositories.converters;

import com.shopcart.enums.OrderPaymentMethodEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderPaymentMethodConverter implements AttributeConverter<OrderPaymentMethodEnum, String> {

    @Override
    public String convertToDatabaseColumn(OrderPaymentMethodEnum status) {
        return (status != null) ? status.getValue() : null;
    }

    @Override
    public OrderPaymentMethodEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? OrderPaymentMethodEnum.fromValue(dbValue) : null;
    }
}