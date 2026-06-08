package com.example.restservice.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.order.dto.CreateOrderRequest;
import com.example.restservice.order.dto.OrderResponse;
import com.example.restservice.shared.security.AuthUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Crée une commande pour l'utilisateur authentifié (id extrait du JWT). */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        Long clientId = currentUserId(authentication);
        Order order = orderService.createOrder(clientId, request.items(), request.shippingAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    /** Liste les commandes de l'utilisateur authentifié. */
    @GetMapping
    public List<OrderResponse> myOrders(Authentication authentication) {
        return orderService.findByClient(currentUserId(authentication)).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id, Authentication authentication) {
        Order order = orderService.getById(id);
        AuthUtils.ensureOwnerOrAdmin(order.getClient().getId(), authentication);
        return OrderResponse.from(order);
    }

    /** Changement de statut (ex. expédition, livraison) : réservé à l'admin. */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return OrderResponse.from(orderService.updateStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, Authentication authentication) {
        Order order = orderService.getById(id);
        AuthUtils.ensureOwnerOrAdmin(order.getClient().getId(), authentication);
        return OrderResponse.from(orderService.cancel(id));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
