package com.order.orders.service;

import com.order.orders.dto.OrderDto;
import com.order.orders.dto.OrderRequest;
import com.order.orders.entity.Order;
import com.order.orders.events.OrderCreatedEvent;
import com.order.orders.exception.OrderNotFoundException;
import com.order.orders.kafka.OrderProducer;
import com.order.orders.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final OrderProducer orderProducer;

    @Autowired
    public OrderService(OrderRepo orderRepo, OrderProducer orderProducer) {
        this.orderRepo = orderRepo;
        this.orderProducer = orderProducer;
    }

    public Order createOrder(OrderRequest request) {
        //return orderRepo.save(order);
//        Order savedOrder = orderRepo.save(order);
//
//        OrderCreatedEvent event = new OrderCreatedEvent();
//        event.setOrderId(savedOrder.getId());
//        //event.setUserId(savedOrder.getUserId());
//        event.setTotal(savedOrder.getTotal());
        Order order = new Order();
        order.setTotal(request.getTotal());
        order.setStatus(request.getStatus());

        Order savedOrder = orderRepo.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setTotal(savedOrder.getTotal());

        orderProducer.sendOrder(event);

        return savedOrder;
    }

    public Order updateOrder(Order order) {
        return orderRepo.save(order);
    }

    public Order findByOrderId(Long orderId) {
        return orderRepo.findById(orderId)
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
