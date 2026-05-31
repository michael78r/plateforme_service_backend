package com.example.restservice.repository.payment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.restservice.model.payment.Payment;

public class PaymentRepository extends JpaRepository<Payment, Long> {

}
