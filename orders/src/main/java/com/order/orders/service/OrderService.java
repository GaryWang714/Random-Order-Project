package com.order.orders.service;

import com.order.orders.entity.Order;
import com.order.orders.exception.OrderNotFoundException;
import com.order.orders.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepo orderRepo;

    @Autowired
    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Order createOrder(Order order) {
        return orderRepo.save(order);
    }

    public Order updateOrder(Order order) {
        return orderRepo.save(order);
    }

    public Order findByOrderId(Long orderId) {
        return orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("order was not found with id: " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public void deleteOrder(Long orderId) {
        orderRepo.deleteById(orderId);
    }

//    public Order updateOrderStatus(Long orderId, String status) {
//
//    }
}
