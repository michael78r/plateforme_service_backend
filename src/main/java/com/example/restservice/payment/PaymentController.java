package com.example.restservice.payment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.order.Order;
import com.example.restservice.order.OrderService;
import com.example.restservice.payment.dto.PaymentRequest;
import com.example.restservice.payment.dto.PaymentResponse;
import com.example.restservice.shared.security.AuthUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    /** Paie une commande en attente (réservé au propriétaire de la commande ou à l'admin). */
    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request, Authentication authentication) {
        Order order = orderService.getById(orderId);
        AuthUtils.ensureOwnerOrAdmin(order.getClient().getId(), authentication);
        Payment payment = paymentService.payOrder(orderId, request.method());
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(payment));
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> byOrder(@PathVariable Long orderId, Authentication authentication) {
        Order order = orderService.getById(orderId);
        AuthUtils.ensureOwnerOrAdmin(order.getClient().getId(), authentication);
        return paymentService.findByOrder(orderId).stream().map(PaymentResponse::from).toList();
    }
}
