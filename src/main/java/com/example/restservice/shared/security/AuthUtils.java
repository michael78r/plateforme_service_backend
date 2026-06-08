package com.example.restservice.shared.security;

import org.springframework.security.core.Authentication;

import com.example.restservice.shared.exception.ForbiddenException;

/** Helpers pour exploiter l'utilisateur courant authentifié (id + rôle) issu du JWT. */
public final class AuthUtils {

    private AuthUtils() {
    }

    /** Id de l'utilisateur courant (le {@code subject} du JWT est l'id en base). */
    public static Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }

    /** Vrai si l'utilisateur courant a le rôle admin. */
    public static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Vérifie que l'utilisateur courant est soit l'admin, soit le propriétaire de la ressource.
     * Lève {@link ForbiddenException} (HTTP 403) sinon.
     */
    public static void ensureOwnerOrAdmin(Long ownerId, Authentication authentication) {
        if (!isAdmin(authentication) && !currentUserId(authentication).equals(ownerId)) {
            throw new ForbiddenException("Accès refusé à cette ressource");
        }
    }
}
