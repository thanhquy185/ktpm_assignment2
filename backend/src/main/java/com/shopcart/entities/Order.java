package com.shopcart.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopcart.enums.OrderStatusEnum;
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

    @Column(nullable = false)
    private String shippingAddress;

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
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> orderItems;
}
