package com.example.restservice.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken>findAllByUtilisateurAndRevokedFalse(Utilisateur utilisateur);
    void deleteAllByUtilisateur(Utilisateur utilisateur);
}
