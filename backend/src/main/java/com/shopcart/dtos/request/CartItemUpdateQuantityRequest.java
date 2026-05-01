package com.shopcart.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
public class CartItemUpdateQuantityRequest {
    @NotNull(message = "Product ID is required!")
    private String productId;

    @NotNull(message = "Quantity is required!")
    @Min(value = 1, message = "Quantity must be at least 1!")
    private Long quantity;
}
