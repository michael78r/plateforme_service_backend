package com.example.restservice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restservice.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
