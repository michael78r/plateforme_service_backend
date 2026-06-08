package com.example.restservice.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.restservice.order.OrderStatus;

/** Indicateurs agrégés pour le tableau de bord administrateur. */
public record DashboardStats(
        BigDecimal totalRevenue,
        long totalOrders,
        Map<OrderStatus, Long> ordersByStatus,
        long totalProducts,
        long lowStockProducts,
        long totalClients,
        List<TopProduct> topProducts) {
}
