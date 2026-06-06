package com.example.restservice.service.client;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.restservice.model.client.Client;
import com.example.restservice.repository.client.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Récupérer tous les clients
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Récupérer un client par ID
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    // Créer un client
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // Mettre à jour un client
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
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