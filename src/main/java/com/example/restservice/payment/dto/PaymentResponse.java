package com.example.restservice.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.restservice.payment.Payment;
import com.example.restservice.payment.PaymentMethod;
import com.example.restservice.payment.PaymentStatus;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod method,
        String transactionRef,
        Instant createdAt,
        Instant paidAt) {

    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getOrder().getId(),
                p.getAmount(),
                p.getStatus(),
                p.getMethod(),
                p.getTransactionRef(),
                p.getCreatedAt(),
                p.getPaidAt());
    }
}
