package com.example.restservice.order;

public enum OrderStatus {
    PENDING,    // créée, en attente de paiement
    PAID,       // payée
    SHIPPED,    // expédiée
    DELIVERED,  // livrée
    CANCELLED,  // annulée
    REFUNDED    // remboursée
}
