package com.shopcart.services;

import com.shopcart.dtos.request.OrderCancelRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.entities.Coupon;
import com.shopcart.entities.Order;
import com.shopcart.entities.OrderItem;
import com.shopcart.entities.Product;
import com.shopcart.enums.OrderStatusEnum;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.OrderAlreadyCancelled;
import com.shopcart.exceptions.OrderItemPriceGreaterThanOrEqualZero;
import com.shopcart.exceptions.OrderItemQuantityGreaterThanZero;
import com.shopcart.exceptions.OrderNotFound;
import com.shopcart.exceptions.ProductNotFoundInInventory;
import com.shopcart.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final UserService userService;
    private final CartService cartService;
    private final CouponService couponService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;

    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    public Order getOrderById(UUID id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new OrderNotFound(id));
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return this.orderRepository.findByUserId(userId);
    }

    public void checkQuantityAndPriceBeforeOrder(List<OrderItemRequest> orderItems) {
        orderItems.stream().forEach(orderItem -> {
            if (orderItem.getQuantity() <= 0) {
                throw new OrderItemQuantityGreaterThanZero();
            }
            if (orderItem.getPrice() <= 0) {
                throw new OrderItemPriceGreaterThanOrEqualZero();
            }
        });
    }

    public void checkStockBeforeOrder(List<OrderItemRequest> orderItems) {
        for (OrderItemRequest orderItem : orderItems) {
            Product product = this.productService.getProductById(
                    UUID.fromString(orderItem.getProductId()));
            if (product.getInventory() == null) {
                throw new ProductNotFoundInInventory(product.getId());
            }
            if (orderItem.getQuantity() > product.getInventory().getStock()) {
                throw new InsufficientStock(product.getId());
            }
        }
    }

    public Double calculateOrderTotal(Long subtotal, Double discount, Long shippingFee) {
        return subtotal - discount + shippingFee;
    }

    public Order createOrder(OrderCreateRequest request) {
        this.checkQuantityAndPriceBeforeOrder(request.getOrderItems());
        this.checkStockBeforeOrder(request.getOrderItems());

        LocalDateTime currentDateTime = LocalDateTime.now();

        Order order = new Order();
        order.setUser(this.userService.getUserById(UUID.fromString(request.getUserId())));
        order.setCreatedAt(currentDateTime);
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingMethod(request.getShippingMethod());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatusEnum.PENDING);
        // - Tổng tiền sản phẩm
        List<OrderItem> orderItems = request.getOrderItems().stream().map(orderItem -> {
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setOrder(order);
            newOrderItem.setProduct(this.productService.getProductById(UUID.fromString(orderItem.getProductId())));
            newOrderItem.setQuantity(orderItem.getQuantity());
            newOrderItem.setPrice(orderItem.getPrice());

            return newOrderItem;
        }).toList();
        Long subtotal = orderItems.stream()
                .map(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .reduce(0L, Long::sum);
        order.setOrderItems(orderItems);
        order.setSubtotal(subtotal);
        // - Số tiền giảm giá
        Coupon coupon = request.getCouponId() != null && !request.getCouponId().isEmpty()
                ? this.couponService.getCouponById(UUID.fromString(request.getCouponId()))
                : null;
        Double discount = 0D;
        if (coupon != null) {
            this.couponService.checkOutOfDate(currentDateTime.toLocalDate(),
                    coupon.getCode(),
                    coupon.getDateStart(),
                    coupon.getDateEnd());

            discount = this.couponService.calculateDiscount(
                    coupon.getType().getValue(),
                    coupon.getDiscount(),
                    subtotal);
        }
        order.setCoupon(coupon);
        order.setDiscount(discount);
        // - Chi phí giao hàng
        order.setShippingFee(request.getShippingFee());
        // - Tổng tiền đơn hàng
        order.setTotalPrice(this.calculateOrderTotal(subtotal, discount, request.getShippingFee()));

        request.getOrderItems().forEach(
                orderItem -> this.inventoryService.decreaseStock(UUID.fromString(orderItem.getProductId()),
                        orderItem.getQuantity()));

        this.cartService.clearCart(UUID.fromString(request.getUserId()));

        return this.orderRepository.save(order);
    }

    public Order cancelOrder(OrderCancelRequest request) {
        UUID orderId = UUID.fromString(request.getOrderId());
        Order order = this.getOrderById(orderId);
        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            throw new OrderAlreadyCancelled(orderId);
        }

        order.getOrderItems().forEach(
                orderItem -> this.inventoryService.increaseStock(
                        orderItem.getProduct().getId(),
                        orderItem.getQuantity()));
        order.setStatus(OrderStatusEnum.CANCELLED);

        return orderRepository.save(order);
    }
}
