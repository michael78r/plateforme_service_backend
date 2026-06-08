package com.example.restservice.catalog.dto;

import com.example.restservice.catalog.Category;

public record CategoryResponse(
        Long id,
        String name,
        String description) {

    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
