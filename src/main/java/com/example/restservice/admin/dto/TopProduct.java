package com.example.restservice.admin.dto;

/** Produit le plus vendu : identifiant, nom et quantité totale écoulée. */
public record TopProduct(
        Long productId,
        String name,
        Long quantitySold) {
}
