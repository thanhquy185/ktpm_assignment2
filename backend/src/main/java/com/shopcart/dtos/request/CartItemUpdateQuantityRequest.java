package com.shopcart.dtos.request;

import jakarta.validation.constraints.NotNull;

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
    private Long quantity;
}
