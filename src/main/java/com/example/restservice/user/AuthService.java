package com.example.restservice.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.restservice.shared.security.JwtUtil;

@Service
public class AuthService {
    private final UtilisateurRepository utilisateurRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final JwtUtil jwtutil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UtilisateurRepository utilisateurRepository, RefreshTokenRepository refreshTokenRepository,
            TokenService tokenService, JwtUtil jwtutil, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.jwtutil = jwtutil;
        this.passwordEncoder = passwordEncoder;
    }

    public Utilisateur register(String email, String plainPassword, String prenom, String nom, String role, String telephone, String adresse, String experience) {
        if (utilisateurRepository.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Email already in use");
        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setMotDePasse(passwordEncoder.encode(plainPassword));
        u.setPrenom(prenom);
        u.setNom(nom);
        u.setRole(resolveRole(role));
        u.setTelephone(telephone);
        u.setAdresse(adresse);
        u.setExperience(experience);
        return utilisateurRepository.save(u);
    }

    /**
     * Résout le rôle demandé à l'inscription. Par défaut {@code client}.
     * Le rôle {@code admin} ne peut PAS être obtenu via l'inscription publique
     * (il est rétrogradé en {@code client}) pour éviter toute élévation de privilège.
     */
    private RoleType resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return RoleType.client;
        }
        RoleType requested;
        try {
            requested = RoleType.valueOf(role.trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return RoleType.client;
        }
        return requested == RoleType.admin ? RoleType.client : requested;
    }

    public Map<String, String> login(String email, String plainPassword) {
        Utilisateur u = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(plainPassword, u.getMotDePasse()))
            throw new IllegalArgumentException("Invalid credentials");
        String access = jwtutil.generateAccessToken(u.getId().toString(),
                Map.of("role", u.getRole().name(), "email", u.getEmail()));

        String refreshPlain = tokenService.createRefreshTokenPlain(u);
        return Map.of("accessToken", access, "refreshToken", refreshPlain);
    }

    public Map<String, String> refresh(String incomingRefreshPlain) {
        Optional<RefreshToken> opt = tokenService.findByTokenPlain(incomingRefreshPlain);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Invalid refresh token");
        RefreshToken found = opt.get();

        if (found.getRevoked() == true || found.getExpireAt().isBefore(java.time.Instant.now())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Utilisateur u = found.getUtilisateur();

        String newRefreshPlain = tokenService.rotateRefreshToken(found, u);

        String newAccess = jwtutil.generateAccessToken(u.getId().toString(),
                Map.of("role", u.getRole().name(), "email", u.getEmail()));

        return Map.of("accessToken", newAccess, "refreshToken", newRefreshPlain);
    }

    public void logout(String incomingRefreshPlain) {
        tokenService.findByTokenPlain(incomingRefreshPlain).ifPresent(t -> {
            tokenService.revoke(t, null);
        });
    }

}
