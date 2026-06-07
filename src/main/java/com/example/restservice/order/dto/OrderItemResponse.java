package com.example.restservice.order.dto;

import java.math.BigDecimal;

import com.example.restservice.order.OrderItem;

public record OrderItemResponse(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal());
    }
}
