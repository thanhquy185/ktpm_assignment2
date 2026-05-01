package com.shopcart.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopcart.services.InventoryService;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkStock(
            @PathVariable String productId,
            @RequestParam Long quantity) {
        // try {
        // boolean available = inventoryService.isAvailable(productId, quantity);
        // return ResponseEntity.ok(new StockCheckResponse(available, productId,
        // quantity));
        // } catch (RuntimeException e) {
        // return ResponseEntity.badRequest()
        // .body(new ErrorResponse(false, e.getMessage()));
        // }

        return null;

    }
}
