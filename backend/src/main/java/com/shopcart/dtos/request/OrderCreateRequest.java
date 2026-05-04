package com.shopcart.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Convert;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;
import com.shopcart.repositories.converters.OrderPaymentMethodConverter;
import com.shopcart.repositories.converters.OrderShippingMethodConverter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {
    @NotNull(message = "User ID is required!")
    private String userId;

    // @NotNull(message = "Coupon ID is required!")
    private String couponId;

    @NotNull(message = "Shipping address is required!")
    private String shippingAddress;

    @NotNull(message = "Shipping method is required!")
    @Convert(converter = OrderShippingMethodConverter.class)
    private OrderShippingMethodEnum shippingMethod;

    @NotNull(message = "Shipping fee is required!")
    @Min(value = 0, message = "Shipping fee must be non-negative!")
    private Long shippingFee;

    @NotNull(message = "Payment method is required!")
    @Convert(converter = OrderPaymentMethodConverter.class)
    private OrderPaymentMethodEnum paymentMethod;

    @NotEmpty(message = "Order items cannot be empty!")
    @Valid
    private List<OrderItemRequest> orderItems;
}
