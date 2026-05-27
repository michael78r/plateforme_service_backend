package com.example.restservice.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.model.user.Utilisateur;
import com.example.restservice.repository.user.UtilisateurRepository;


@RestController
@RequestMapping("/user")

public class UtilisateurController {
    
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/all")
    public Iterable<Utilisateur> getAllUser(){
        return utilisateurRepository.findAll();
    }
    
    
    
}
