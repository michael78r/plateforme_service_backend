package com.example.restservice.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restservice.model.client.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}