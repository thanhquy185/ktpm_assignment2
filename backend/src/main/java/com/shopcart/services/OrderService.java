package com.shopcart.services;

import com.shopcart.dtos.request.OrderItemRequest;
import com.shopcart.dtos.request.OrderCancelRequest;
import com.shopcart.dtos.request.OrderCreateRequest;
import com.shopcart.entities.Coupon;
import com.shopcart.entities.Order;
import com.shopcart.entities.OrderItem;
import com.shopcart.enums.OrderStatusEnum;
import com.shopcart.exceptions.InsufficientStock;
import com.shopcart.exceptions.OrderAlreadyCancelled;
import com.shopcart.exceptions.OrderNotFound;
import com.shopcart.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final UserService userService;
    private final CouponService couponService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;

    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    public Order getOrderById(String id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new OrderNotFound(id));
    }

    public List<Order> getOrderByUserId(String userId) {
        return this.orderRepository.findByUserId(userId);
    }

    public void checkStockBeforeOrder(List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            boolean isAvailable = this.inventoryService.isAvailable(
                    item.getProductId(),
                    item.getQuantity());
            if (!isAvailable) {
                throw new InsufficientStock(item.getProductId());
            }
        }
    }

    public Double calculateOrderTotal(Long subtotal, Double discount, Long shippingFee) {
        return subtotal - discount + shippingFee;
    }

    public Order createOrder(String userId, OrderCreateRequest request) {
        this.checkStockBeforeOrder(request.getOrderItems());

        Order order = new Order();
        order.setUser(this.userService.getUserById(userId));
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatusEnum.PENDING);
        // - Tổng tiền sản phẩm
        List<OrderItem> orderItems = request.getOrderItems().stream().map(orderItem -> {
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setOrder(order);
            newOrderItem.setProduct(this.productService.getProductById(orderItem.getProductId()));
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
        Coupon coupon = this.couponService.getCouponById(request.getCouponId());
        Double discount = this.couponService.calculateDiscount(
                coupon.getType().getValue(),
                coupon.getDiscount(),
                subtotal);
        order.setCoupon(coupon);
        order.setDiscount(discount);
        // - Chi phí giao hàng
        order.setShippingFee(request.getShippingFee());
        // - Tổng tiền đơn hàng
        order.setTotalPrice(this.calculateOrderTotal(subtotal, discount, request.getShippingFee()));

        request.getOrderItems().forEach(
                i -> this.inventoryService.decreaseStock(i.getProductId(), i.getQuantity()));

        return this.orderRepository.save(order);
    }

    public Order cancelOrder(OrderCancelRequest request) {
        Order order = this.getOrderById(request.getOrderId());
        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            throw new OrderAlreadyCancelled(request.getOrderId());
        }

        order.getOrderItems().forEach(
                orderItem -> this.inventoryService.increaseStock(
                        orderItem.getProduct().getId(),
                        orderItem.getQuantity()));
        order.setStatus(OrderStatusEnum.CANCELLED);

        return orderRepository.save(order);
    }
}
