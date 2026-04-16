package com.payment.payments.service;

import com.payment.payments.entity.Payment;
import com.payment.payments.enums.StatusEnum;
import com.payment.payments.mock.MockPaymentGateway;
import com.payment.payments.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;

    private MockPaymentGateway mockGateway;

    public PaymentService(PaymentRepo paymentRepo, MockPaymentGateway mockGateway) {
        this.paymentRepo = paymentRepo;
        this.mockGateway = mockGateway;
    }

    public Payment createPayment(Long orderId, BigDecimal amount, String currency) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(StatusEnum.PENDING);
        payment.setCurrency(currency);

        boolean success = mockGateway.charge(amount, currency);

        payment.setStatus(success ? StatusEnum.SUCCESS : StatusEnum.FAILED);

        return paymentRepo.save(payment);
    }

    public Payment markSuccess(String paymentId) {
        Payment payment = paymentRepo.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        payment.setStatus(StatusEnum.SUCCESS);

        return paymentRepo.save(payment);
    }

    public Payment markFailed(String paymentId) {
        Payment payment = paymentRepo.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found:" + paymentId));

        payment.setStatus(StatusEnum.FAILED);

        return paymentRepo.save(payment);
    }

    public Payment getPayment(String paymentId) {
        return paymentRepo.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

}
