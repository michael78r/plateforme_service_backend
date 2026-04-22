package com.example.restservice.controller.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.service.user.AuthService;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        var user = authService.register(body.get("email"), body.get("password"), body.get("prenom"), body.get("nom"), body.get("role"), body.get("telephone"), body.get("adresse"), Integer.parseInt(body.get("id_specialite")), body.get("experience"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        var tokens = authService.login(body.get("email"), body.get("password"));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body) {
        var tokens = authService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String,String> body) {
        authService.logout(body.get("refreshToken"));
        return ResponseEntity.ok(Map.of("status", "logged out"));
    }
}