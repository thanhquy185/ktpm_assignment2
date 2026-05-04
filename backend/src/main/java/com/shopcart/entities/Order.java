package com.shopcart.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopcart.enums.OrderPaymentMethodEnum;
import com.shopcart.enums.OrderShippingMethodEnum;
import com.shopcart.enums.OrderStatusEnum;
import com.shopcart.repositories.converters.OrderPaymentMethodConverter;
import com.shopcart.repositories.converters.OrderShippingMethodConverter;
import com.shopcart.repositories.converters.OrderStatusConverter;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String shippingAddress;

    @Convert(converter = OrderShippingMethodConverter.class)
    @Column(nullable = false)
    private OrderShippingMethodEnum shippingMethod;

    @Convert(converter = OrderPaymentMethodConverter.class)
    @Column(nullable = false)
    private OrderPaymentMethodEnum paymentMethod;

    @Column(nullable = false)
    private Long subtotal;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false)
    private Long shippingFee;

    @Column(nullable = false)
    private Double totalPrice;

    @Convert(converter = OrderStatusConverter.class)
    @Column(nullable = false)
    private OrderStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "role", "username", "password", "cart", "orders" })
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}
