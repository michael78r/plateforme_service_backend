package com.example.restservice.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.example.restservice.order.Order;
import com.example.restservice.order.OrderStatus;

public record OrderResponse(
        Long id,
        Long clientId,
        OrderStatus status,
        BigDecimal total,
        String shippingAddress,
        Instant createdAt,
        List<OrderItemResponse> items) {

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getClient().getId(),
                order.getStatus(),
                order.getTotal(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items);
    }
}
