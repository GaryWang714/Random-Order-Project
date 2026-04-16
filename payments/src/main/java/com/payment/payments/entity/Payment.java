package com.payment.payments.entity;

import com.payment.payments.enums.StatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {
    @Id
    private String paymentId;

    @Indexed(unique = true)
    private Long orderId;

    private String currency;
    private BigDecimal amount;

    private StatusEnum status;
}
