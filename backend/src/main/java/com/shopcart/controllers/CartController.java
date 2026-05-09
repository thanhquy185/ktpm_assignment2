package com.shopcart.controllers;

import com.shopcart.dtos.request.CartItemAddToCartRequest;
import com.shopcart.dtos.request.CartItemRemoveFromCartRequest;
import com.shopcart.dtos.request.CartItemUpdateQuantityRequest;
import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.Cart;
import com.shopcart.exceptions.CartItemNotFound;
import com.shopcart.exceptions.CartItemQuantityGreaterThanZero;
import com.shopcart.exceptions.CartNotFound;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.exceptions.UserNotFound;
import com.shopcart.exceptions.UserNotFoundInCart;
import com.shopcart.services.CartService;
import com.shopcart.utils.ValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {
        private final CartService cartService;

        @GetMapping("")
        public ResponseEntity<?> getAllCart() {
                List<Cart> carts = this.cartService.getAllCart();

                RestResponse<List<Cart>> restResponse = RestResponse.<List<Cart>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Get all cart is successful!")
                                .data(carts)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getCartById(@PathVariable("id") String id) {
                Cart cart = this.cartService.getCartById(UUID.fromString(id));

                RestResponse<Cart> restResponse = RestResponse.<Cart>builder()
                                .status(HttpStatus.OK.value())
                                .message("Get cart by id is successful!")
                                .data(cart)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<?> getCartByUserId(@PathVariable("userId") String userId) {
                Cart cart = this.cartService.getCartByUserId(UUID.fromString(userId));

                RestResponse<Cart> restResponse = RestResponse.<Cart>builder()
                                .status(HttpStatus.OK.value())
                                .message("Get cart by user id is successful!")
                                .data(cart)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @PostMapping("/user/{userId}")
        public ResponseEntity<?> addToCart(
                        @PathVariable("userId") String userId,
                        @RequestBody @Valid CartItemAddToCartRequest request,
                        BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                        System.out.println(bindingResult.hasErrors());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ValidationUtil.buildRestResponse(bindingResult));
                }

                this.cartService.addToCart(UUID.fromString(userId), request);
                Cart cart = this.cartService.getCartByUserId(UUID.fromString(userId));

                RestResponse<Cart> restResponse = RestResponse.<Cart>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Add product to cart is successful!")
                                .data(cart)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
        }

        @PutMapping("/user/{userId}")
        public ResponseEntity<?> updateQuantity(
                        @PathVariable("userId") String userId,
                        @RequestBody @Valid CartItemUpdateQuantityRequest request,
                        BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                        System.out.println(bindingResult.hasErrors());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ValidationUtil.buildRestResponse(bindingResult));
                }

                this.cartService.updateQuantity(UUID.fromString(userId), request);
                Cart cart = this.cartService.getCartByUserId(UUID.fromString(userId));

                RestResponse<Cart> restResponse = RestResponse.<Cart>builder()
                                .status(HttpStatus.OK.value())
                                .message("Update product quantity in cart is successful!")
                                .data(cart)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @DeleteMapping("/user/{userId}")
        public ResponseEntity<?> removeFromCart(
                        @PathVariable("userId") String userId,
                        @RequestBody @Valid CartItemRemoveFromCartRequest request,
                        BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                        System.out.println(bindingResult.hasErrors());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ValidationUtil.buildRestResponse(bindingResult));
                }

                this.cartService.removeFromCart(UUID.fromString(userId), request);
                Cart cart = this.cartService.getCartByUserId(UUID.fromString(userId));

                RestResponse<Cart> restResponse = RestResponse.<Cart>builder()
                                .status(HttpStatus.OK.value())
                                .message("Remove product from cart is successful!")
                                .data(cart)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        }

        @ExceptionHandler(CartNotFound.class)
        public ResponseEntity<?> handleCartNotFound(CartNotFound e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("CART_NOT_FOUND")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
        }

        @ExceptionHandler(UserNotFound.class)
        public ResponseEntity<?> handleUserNotFound(UserNotFound e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("USER_NOT_FOUND")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
        }

        @ExceptionHandler(UserNotFoundInCart.class)
        public ResponseEntity<?> handleUserNotFoundInCart(UserNotFoundInCart e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("USER_NOT_FOUND_IN_CART")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
        }

        @ExceptionHandler(CartItemQuantityGreaterThanZero.class)
        public ResponseEntity<?> handleCartItemQuantityGreaterThanZero(CartItemQuantityGreaterThanZero e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("CART_ITEM_QUANTITY_GREATER_THAN_ZERO")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
        }

        @ExceptionHandler(InsufficientStock.class)
        public ResponseEntity<?> handleInsufficientStock(InsufficientStock e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("INSUFFICIENT_STOCK")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
        }

        @ExceptionHandler(CartItemNotFound.class)
        public ResponseEntity<?> handleCartItemNotFound(CartItemNotFound e) {
                RestResponse<Object> restResponse = RestResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("CART_ITEM_NOT_FOUND")
                                .message(e.getMessage())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
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
