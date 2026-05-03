package com.shopcart.controllers;

import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.Product;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.services.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {
        private final ProductService productService;

        @GetMapping("")
        public ResponseEntity<?> getAllProduct() {
                List<Product> products = this.productService.getAllProduct();

                RestResponse<List<Product>> restResponse = RestResponse.<List<Product>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Get all product is successful!")
                                .data(products)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getProductById(@PathVariable("id") String id) {
                Product product = this.productService.getProductById(UUID.fromString(id));

                RestResponse<Product> restResponse = RestResponse.<Product>builder()
                                .status(HttpStatus.OK.value())
                                .message("Get product by id is successful!")
                                .data(product)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
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
}
