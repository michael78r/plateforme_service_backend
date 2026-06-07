package com.example.restservice.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public class PaymentRepository extends JpaRepository<Payment, Long> {
    public Payment findByOrderId(String orderId) {
        // Simulate finding a payment by order ID
        return new Payment("1", orderId, 100.0);
    }

    public Payment findById(String paymentId) {
        // Simulate finding a payment by ID
        return new Payment(paymentId, "order123", 100.0);
    }

    public void save(Payment payment) {
        // Simulate saving a payment
        System.out.println("Payment with ID " + payment.getId() + " saved successfully.");
    }

    public void deleteById(String paymentId) {
        // Simulate deleting a payment by ID
        System.out.println("Payment with ID " + paymentId + " deleted successfully.");
    }

}
