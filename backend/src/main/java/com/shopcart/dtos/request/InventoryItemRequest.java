package com.shopcart.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemRequest {
    @NotNull(message = "Product ID is required!")
    private String productId;

    @NotNull(message = "Quantity is required!")
    private Long quantity;

}
