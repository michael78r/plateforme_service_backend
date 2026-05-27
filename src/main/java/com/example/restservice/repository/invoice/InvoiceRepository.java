package com.example.restservice.repository.invoice;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restservice.model.Invoice.Invoice;

public class InvoiceRepository extends JpaRepository<Invoice, Long> {
    
}
