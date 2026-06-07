package com.example.restservice.invoice;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restservice.order.Order;
import com.example.restservice.shared.exception.ResourceNotFoundException;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /** Génère (ou retourne) la facture associée à une commande payée. Idempotent. */
    @Transactional
    public Invoice generateForOrder(Order order) {
        return invoiceRepository.findByOrderId(order.getId())
                .orElseGet(() -> {
                    String number = "INV-" + order.getId();
                    Invoice invoice = new Invoice(order, number, order.getTotal());
                    invoice.setStatus(InvoiceStatus.PAID);
                    return invoiceRepository.save(invoice);
                });
    }

    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Invoice getById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Invoice getByOrder(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune facture pour la commande : " + orderId));
    }
}
