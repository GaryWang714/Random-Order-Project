package com.payment.payments.controller;

import com.payment.payments.dto.PaymentDto;
import com.payment.payments.entity.Payment;
import com.payment.payments.service.PaymentService;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment createPayment(@RequestBody PaymentDto request) {
        return paymentService.createPayment(
                request.getOrderId(),
                request.getAmount(),
                request.getCurrency()
        );
    }

    @PostMapping("/{id}/success")
    public Payment markSuccess(@PathVariable String id) {
        return paymentService.markSuccess(id);
    }

    @PostMapping("/{id}/fail")
    public Payment markFailed(@PathVariable String id) {
        return paymentService.markFailed(id);
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable String id) {
        return paymentService.getPayment(id);
    }

}
