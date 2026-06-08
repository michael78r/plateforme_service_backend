package com.example.restservice.payment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restservice.invoice.InvoiceService;
import com.example.restservice.order.Order;
import com.example.restservice.order.OrderRepository;
import com.example.restservice.order.OrderStatus;
import com.example.restservice.shared.exception.BusinessException;
import com.example.restservice.shared.exception.ResourceNotFoundException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InvoiceService invoiceService;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
            InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.invoiceService = invoiceService;
    }

    /**
     * Paie une commande en attente. Le traitement PSP est ici simulé (toujours réussi) :
     * à remplacer par l'intégration d'un vrai prestataire (Stripe, PayPal...).
     * En cas de succès : passe la commande à PAID et génère la facture.
     */
    @Transactional
    public Payment payOrder(Long orderId, PaymentMethod method) {
        // Verrou exclusif : garantit l'idempotence face à des appels concurrents (anti double-paiement).
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Cette commande n'est pas en attente de paiement");
        }

        Payment payment = new Payment(order, order.getTotal(), method);
        // --- Simulation du PSP ---
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(Instant.now());
        payment.setTransactionRef("SIMU-" + UUID.randomUUID());
        // -------------------------
        Payment saved = paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        invoiceService.generateForOrder(order);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Payment> findByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable : " + id));
    }
}
