package com.order.orders.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "Total is required")
    @Positive(message = "Total must be greater than zero")
    @DecimalMax(value = "10000.00", message = "Total cannot exceed 10000")
    @Digits(integer = 6, fraction = 2, message = "Total must have at most 2 decimal places")
    private BigDecimal total;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PENDING|CONFIRMED|CANCELLED)$", message = "Status must be PENDING, CONFIRMED, or CANCELLED")
    private String status;
}
