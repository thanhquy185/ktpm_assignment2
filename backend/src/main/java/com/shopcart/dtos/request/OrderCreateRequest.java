package com.shopcart.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {
    @NotNull(message = "Coupon ID is required!")
    private String couponId;

    @NotNull(message = "Shipping address is required!")
    private String shippingAddress;

    @NotNull(message = "Shipping fee is required!")
    @Min(value = 0, message = "Shipping fee must be non-negative!")
    private Long shippingFee;

    @NotEmpty(message = "Order items cannot be empty!")
    @Valid
    private List<OrderItemRequest> orderItems;
}
