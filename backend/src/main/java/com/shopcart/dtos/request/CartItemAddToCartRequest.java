package com.shopcart.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
public class CartItemAddToCartRequest {
    @NotNull(message = "Product ID is required!")
    private String productId;

    @NotNull(message = "Quantity is required!")
    private Long quantity;
}
