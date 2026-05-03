package com.shopcart.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.shopcart.dtos.request.InventoryCheckStockRequest;
import com.shopcart.dtos.response.RestResponse;
import com.shopcart.exceptions.InventoryItemQuantityGreaterThanZero;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.services.InventoryService;
import com.shopcart.utils.ValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("")
    public ResponseEntity<?> checkStock(
            @RequestBody @Valid InventoryCheckStockRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.hasErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationUtil.buildRestResponse(bindingResult));
        }

        Boolean available = this.inventoryService.checkStock(request);

        RestResponse<Boolean> restResponse = RestResponse.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Check stock for inventory items is available!")
                .data(available)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @ExceptionHandler(InventoryItemQuantityGreaterThanZero.class)
    public ResponseEntity<?> handleInventoryItemQuantityGreaterThanZero(InventoryItemQuantityGreaterThanZero e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVENTORY_ITEM_QUANTITY_GREATER_THAN_ZERO")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<?> handleProductNotFound(ProductNotFound e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("PRODUCT_NOT_FOUND")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(ProductNotFoundInInventory.class)
    public ResponseEntity<?> handleProductNotFoundInInventory(ProductNotFoundInInventory e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("PRODUCT_NOT_FOUND_IN_INVENTORY")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }
}
