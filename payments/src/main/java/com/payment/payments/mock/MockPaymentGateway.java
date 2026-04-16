package com.payment.payments.mock;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Component
public class MockPaymentGateway {

    private final Random random = new Random();

    public boolean charge(BigDecimal amount, String currency) {
        boolean success = random.nextDouble() > 0.2;

        System.out.println("Mock payment processed: ");
        System.out.println("Amount: " + amount);
        System.out.println("Currency: " + currency);
        System.out.println("Result: " + (success ? "SUCCESS" : "FAILED"));

        return success;
    }
//    public String process(Long amount) throws InterruptedException {
//        Thread.sleep((long) (Math.random() * 1000 + 500));
//
//        if(Math.random() < 0.10) {
//            throw new RuntimeException("Declined");
//        }
//
//        return "MOCK " + UUID.randomUUID().toString();
//    }

}
