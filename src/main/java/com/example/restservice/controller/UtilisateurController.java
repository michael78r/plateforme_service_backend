package com.example.restservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.model.Utilisateur;
import com.example.restservice.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
