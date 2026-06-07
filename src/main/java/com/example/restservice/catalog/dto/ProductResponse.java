package com.example.restservice.catalog.dto;

import java.math.BigDecimal;

import com.example.restservice.catalog.Product;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String sku,
        String imageUrl,
        boolean active,
        Long categoryId,
        String categoryName) {

    public static ProductResponse from(Product p) {
        Long categoryId = p.getCategory() != null ? p.getCategory().getId() : null;
        String categoryName = p.getCategory() != null ? p.getCategory().getName() : null;
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStockQuantity(), p.getSku(), p.getImageUrl(), p.isActive(),
                categoryId, categoryName);
    }
}
