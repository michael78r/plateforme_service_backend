package com.example.restservice.service.client;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.restservice.repository.client.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Récupérer tous les clients
    public List<ClientRepository> getAllClients() {
        return clientRepository.findAll();
    }

    // Récupérer un client par ID
    public Optional<ClientRepository> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    // Créer un client
    public ClientRepository createClient(ClientRepository client) {
        return clientRepository.save(client);
    }

    // Mettre à jour un client
    public ClientRepository updateClient(Long id, ClientRepository clientDetails) {
        ClientRepository client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        client.setName(clientDetails.getName());
        client.setEmail(clientDetails.getEmail());
        client.setContactHistory(clientDetails.getContactHistory());

        return clientRepository.save(client);
    }

    // Supprimer un client
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}