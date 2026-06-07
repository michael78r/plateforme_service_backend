package com.example.restservice.catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @PositiveOrZero BigDecimal price,
        @PositiveOrZero int stockQuantity,
        String sku,
        String imageUrl,
        Boolean active,
        Long categoryId) {
}
