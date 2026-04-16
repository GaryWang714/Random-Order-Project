package com.order.orders.kafka;

import com.order.orders.dto.OrderDto;
import com.order.orders.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(OrderCreatedEvent event) {
        kafkaTemplate.send("order-events", event.getOrderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println("[OrderProducer] FAILED to send event: " + ex.getMessage());
                    } else {
                        System.out.println("[OrderProducer] SUCCESS - sent orderId=" + event.getOrderId()
                                + " to partition=" + result.getRecordMetadata().partition()
                                + " offset=" + result.getRecordMetadata().offset());
                    }
                });
    }

}
