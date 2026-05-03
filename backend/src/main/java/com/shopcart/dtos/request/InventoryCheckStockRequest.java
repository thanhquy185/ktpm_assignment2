package com.shopcart.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCheckStockRequest {
    @NotEmpty(message = "Inventory items cannot be empty!")
    @Valid
    private List<InventoryItemRequest> inventoryItems;
}
