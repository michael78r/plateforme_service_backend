package com.example.restservice.payment.dto;

import com.example.restservice.payment.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull PaymentMethod method) {
}
