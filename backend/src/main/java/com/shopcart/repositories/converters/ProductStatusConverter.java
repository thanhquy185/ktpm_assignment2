package com.shopcart.repositories.converters;

import com.shopcart.enums.ProductStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductStatusConverter implements AttributeConverter<ProductStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(ProductStatusEnum status) {
        return (status != null) ? status.getValue() : null;
    }

    @Override
    public ProductStatusEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? ProductStatusEnum.fromValue(dbValue) : null;
    }
}