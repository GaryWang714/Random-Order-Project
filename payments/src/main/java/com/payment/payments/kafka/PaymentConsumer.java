package com.payment.payments.kafka;

import com.payment.payments.events.OrderCreatedEvent;
import com.payment.payments.service.PaymentService;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    private final PaymentService paymentService;

    public PaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
        System.out.println("[PaymentConsumer] Bean created");
    }

    @PostConstruct
    public void init() {
        System.out.println("[PaymentConsumer] Listening on topic: order-events");
    }

    @KafkaListener(topics = "order-events")
    public void consume(OrderCreatedEvent event) {
        try {
            System.out.println("[PaymentConsumer] Message received - orderId=" + event.getOrderId() + ", total=" + event.getTotal());
            paymentService.createPayment(event.getOrderId(), event.getTotal(), "USD");
            System.out.println("[PaymentConsumer] Payment processed for orderId=" + event.getOrderId());
        } catch (Exception e) {
            System.out.println("[PaymentConsumer] ERROR processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
