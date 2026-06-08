package com.example.restservice.shared.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Enveloppe JSON stable pour les résultats paginés.
 * Évite de sérialiser directement {@code Page}/{@code PageImpl} (contrat non garanti par Spring).
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
