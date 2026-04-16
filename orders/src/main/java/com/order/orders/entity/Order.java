package com.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "user_id")
//    private Long userId;
    @Column(name = "total_amt", nullable = false)
    private BigDecimal total;
    @Column(name = "order_status")
    private String status;
}
