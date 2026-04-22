package com.example.restservice.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping
    public Iterable<Utilisateur> getAllUser(){
        return utilisateurRepository.findAll();
    }

    @GetMapping("/test")
    public String getMethodName() {
        return "Received parameter: ";
    }
    

    @PostMapping("login")
    public String login(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }

    @PostMapping("new")
    public String inscription(@RequestBody String entity) {
        return entity;
    }
    
    
    
}
