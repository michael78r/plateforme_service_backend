package com.example.restservice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restservice.model.order.Order;

public class OrderRepository extends JpaRepository<Order, Long> {
    
}
