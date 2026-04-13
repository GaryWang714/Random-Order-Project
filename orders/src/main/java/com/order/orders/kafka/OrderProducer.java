package com.order.orders.kafka;

import com.order.orders.dto.OrderDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(OrderDto orderDto) {
        kafkaTemplate.send(
                "order-events",
                orderDto.getOrderId().toString(),
                orderDto
        );
    }

}
