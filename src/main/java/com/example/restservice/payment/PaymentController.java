package com.example.restservice.payment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.payment.dto.PaymentRequest;
import com.example.restservice.payment.dto.PaymentResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Paie une commande en attente. */
    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.payOrder(orderId, request.method());
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(payment));
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> byOrder(@PathVariable Long orderId) {
        return paymentService.findByOrder(orderId).stream().map(PaymentResponse::from).toList();
    }
}
