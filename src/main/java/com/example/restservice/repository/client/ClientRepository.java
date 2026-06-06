package com.example.restservice.repository.client;

public class ClientRepository extends com.example.restservice.model.client.Client {
    public ClientRepository() {
        super();
    }

    public ClientRepository(String name, String email) {
        super(name, email);
    }
    
}
