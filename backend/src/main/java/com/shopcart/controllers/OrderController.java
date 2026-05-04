package com.shopcart.controllers;

import com.shopcart.dtos.request.OrderCancelRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.Order;
import com.shopcart.exceptions.CouponNotFound;
import com.shopcart.exceptions.CouponOutOfDate;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.InvalidInventoryQuantity;
// import com.shopcart.exceptions.InvalidInventoryQuantity;
import com.shopcart.exceptions.OrderAlreadyCancelled;
import com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero;
import com.shopcart.exceptions.OrderItemQuantityGreaterThanZero;
import com.shopcart.exceptions.OrderNotFound;
import com.shopcart.exceptions.ProductNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.exceptions.UserNotFoundInCart;
import com.shopcart.services.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {
    private final OrderService orderService;

    @GetMapping("")
    public ResponseEntity<?> getAllOrder() {
        List<Order> orders = this.orderService.getAllOrder();

        RestResponse<List<Order>> restResponse = RestResponse.<List<Order>>builder()
                .status(HttpStatus.OK.value())
                .message("Get all orders is successful!")
                .data(orders)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<?> getOrderById(@PathVariable("id") String id) {
    // Order order = this.orderService.getOrderById(UUID.fromString(id));

    // RestResponse<Order> restResponse = RestResponse.<Order>builder()
    // .status(HttpStatus.OK.value())
    // .message("Get order by id is successful!")
    // .data(order)
    // .build();

    // return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    // }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable("userId") String userId) {
        List<Order> orders = this.orderService.getOrderByUserId(UUID.fromString(userId));

        RestResponse<List<Order>> restResponse = RestResponse.<List<Order>>builder()
                .status(HttpStatus.OK.value())
                .message("Get orders by user id is successful!")
                .data(orders)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderCreateRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.hasErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationUtil.buildRestResponse(bindingResult));
        }

        Order order = this.orderService.createOrder(request);

        RestResponse<Order> restResponse = RestResponse.<Order>builder()
                .status(HttpStatus.CREATED.value())
                .message("Create order is successful!")
                .data(order)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
    }

    @DeleteMapping("")
    public ResponseEntity<?> cancelOrder(
            @RequestBody @Valid OrderCancelRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.hasErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationUtil.buildRestResponse(bindingResult));
        }

        Order order = this.orderService.cancelOrder(request);

        RestResponse<Order> restResponse = RestResponse.<Order>builder()
                .status(HttpStatus.OK.value())
                .message("Cancel order is successful!")
                .data(order)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @ExceptionHandler(OrderNotFound.class)
    public ResponseEntity<?> handleOrderNotFound(OrderNotFound e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("ORDER_NOT_FOUND")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(OrderItemQuantityGreaterThanZero.class)
    public ResponseEntity<?> handleOrderItemQuantityGreaterThanZero(OrderItemQuantityGreaterThanZero e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("ORDER_ITEM_QUANTITY_GREATER_THAN_ZERO")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(OrderItemPriceGreaterThanOrEqualZero.class)
    public ResponseEntity<?> handleOrderItemPriceGreaterThanOrEqualZero(OrderItemPriceGreaterThanOrEqualZero e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("ORDER_ITEM_PRICE_GREATER_THAN_OR_EQUAL_ZERO")
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

    @ExceptionHandler(OrderAlreadyCancelled.class)
    public ResponseEntity<?> handleOrderAlreadyCancelled(OrderAlreadyCancelled e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("ORDER_ALREADY_CANCELLED")
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

    @ExceptionHandler(CouponNotFound.class)
    public ResponseEntity<?> handleCouponNotFound(CouponNotFound e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("COUPON_NOT_FOUND")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(CouponOutOfDate.class)
    public ResponseEntity<?> handleCouponOutOfDate(CouponOutOfDate e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("COUPON_OUT_OF_DATE")
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

    @ExceptionHandler(InvalidInventoryQuantity.class)
    public ResponseEntity<?> handleInvalidInventoryQuantity(InvalidInventoryQuantity e) {
        RestResponse<Object> restResponse = RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_INVENTORY_QUANTITY")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
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
}