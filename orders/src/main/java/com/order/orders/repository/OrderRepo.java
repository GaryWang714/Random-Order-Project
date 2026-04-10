package com.order.orders.repository;

import com.order.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    void deleteById(Long orderId);

    Optional<Order> findByOrderId(Long orderId);

    //Optional findOrderById(Long orderId);
}
