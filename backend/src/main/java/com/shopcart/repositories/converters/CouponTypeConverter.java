package com.shopcart.repositories.converters;

import com.shopcart.enums.CouponTypeEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CouponTypeConverter implements AttributeConverter<CouponTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(CouponTypeEnum type) {
        return (type != null) ? type.getValue() : null;
    }

    @Override
    public CouponTypeEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? CouponTypeEnum.fromValue(dbValue) : null;
    }
}