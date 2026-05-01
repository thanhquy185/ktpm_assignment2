package com.shopcart.controllers;

import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.dtos.request.CartItemRemoveFromCartRequest;
import com.shopcart.dtos.request.CartItemUpdateQuantityRequest;
import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.Cart;
import com.shopcart.services.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {
        private final CartService cartService;

        @GetMapping("")
        public ResponseEntity<?> getCarts() {
                List<Cart> carts = this.cartService.getAllCart();

                RestResponse restResponse = RestResponse.builder()
                                .statusCode(HttpStatus.OK)
                                .message("Get all cart is successful!")
                                .data(carts)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @GetMapping("{id}")
        public ResponseEntity<?> getCartById(@PathVariable("id") String id) {
                try {
                        Cart cart = this.cartService.getCartById(id);

                        RestResponse restResponse = RestResponse.builder()

                                        .statusCode(HttpStatus.OK)
                                        .message("Get cart by id is successful!")
                                        .data(cart)
                                        .build();

                        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
                } catch (RuntimeException e) {
                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST)
                                        .message("Get cart by id is unsuccessful!")
                                        .data(null)
                                        .build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
                }
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<?> getCartByUserId(@PathVariable("userId") String userId) {
                try {
                        Cart cart = this.cartService.getCartByUserId(userId);

                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.OK)
                                        .message("Get cart by user id is successful!")
                                        .data(cart)
                                        .build();

                        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
                } catch (RuntimeException e) {
                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST)
                                        .message("Get cart by user id is unsuccessful!")
                                        .data(null)
                                        .build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
                }
        }

        @PostMapping("/user/{userId}/add")
        public ResponseEntity<?> addToCart(
                        @PathVariable("userId") String userId,
                        @Valid @RequestBody CartItemAddToCartRequest request) {
                try {
                        this.cartService.addToCart(userId, request);
                        Cart cart = this.cartService.getCartByUserId(userId);

                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.CREATED)
                                        .message("Add product to cart is successful!")
                                        .data(cart)
                                        .build();

                        return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
                } catch (RuntimeException e) {
                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST)
                                        .message("Add product to cart is unsuccessful!")
                                        .data(null)
                                        .build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
                }
        }

        @PutMapping("/user/{userId}/update")
        public ResponseEntity<?> updateQuantity(
                        @PathVariable("userId") String userId,
                        @Valid @RequestBody CartItemUpdateQuantityRequest request) {
                try {
                        this.cartService.updateQuantity(userId, request);
                        Cart cart = this.cartService.getCartByUserId(userId);

                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.OK)
                                        .message("Update product quantity in cart is successful!")
                                        .data(cart)
                                        .build();

                        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
                } catch (RuntimeException e) {
                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST)
                                        .message("Update product quantity in cart is unsuccessful!")
                                        .data(null)
                                        .build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
                }
        }

        @DeleteMapping("/user/{userId}/remove")
        public ResponseEntity<?> removeFromCart(
                        @PathVariable("userId") String userId,
                        @Valid @RequestBody CartItemRemoveFromCartRequest request) {
                try {
                        this.cartService.removeFromCart(userId, request);
                        Cart cart = this.cartService.getCartByUserId(userId);

                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.OK)
                                        .message("Remove product from cart is successful!")
                                        .data(cart)
                                        .build();

                        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
                } catch (RuntimeException e) {
                        RestResponse restResponse = RestResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST)
                                        .message("Remove product from cart is unsuccessful!")
                                        .data(null)
                                        .build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
                }
        }
}
