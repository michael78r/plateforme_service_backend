package com.example.restservice.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String username);

    /** Nombre d'utilisateurs ayant un rôle donné (ex. nombre de clients). */
    long countByRole(RoleType role);
}
