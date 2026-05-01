package com.shopcart.controllers;

import com.shopcart.dtos.request.OrderCancelRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.dtos.response.RestResponse;
import com.shopcart.entities.Order;
import com.shopcart.services.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {
    private final OrderService orderService;

    @GetMapping("")
    public ResponseEntity<?> getOrders() {
        List<Order> orders = this.orderService.getAllOrder();

        RestResponse restResponse = RestResponse.builder()
                .statusCode(HttpStatus.OK)
                .message("Get all orders is successful!")
                .data(orders)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") String id) {
        try {
            Order order = this.orderService.getOrderById(id);

            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .message("Get order by id is successful!")
                    .data(order)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        } catch (RuntimeException e) {
            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .message("Get order by id is unsuccessful!")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable("userId") String userId) {
        try {
            List<Order> orders = this.orderService.getOrderByUserId(userId);

            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .message("Get orders by user id is successful!")
                    .data(orders)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        } catch (RuntimeException e) {
            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .message("Get orders by user id is unsuccessful!")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
        }
    }

    @PostMapping("/user/{userId}/create")
    public ResponseEntity<?> createOrder(@PathVariable("userId") String userId,
            @Valid @RequestBody OrderCreateRequest request) {
        try {
            Order order = this.orderService.createOrder(userId, request);

            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.CREATED)
                    .message("Create order is successful!")
                    .data(order)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
        } catch (RuntimeException e) {
            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .message("Create order is unsuccessful!")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
        }
    }

    @PutMapping("/user/{userId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable("userId") String userId,
            @Valid @RequestBody OrderCancelRequest request) {
        try {
            Order order = this.orderService.cancelOrder(request);

            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .message("Cancel order is successful!")
                    .data(order)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(restResponse);
        } catch (RuntimeException e) {
            RestResponse restResponse = RestResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .message("Cancel order is unsuccessful!")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
        }
    }
}