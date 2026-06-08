package com.example.restservice.shared.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.restservice.shared.exception.ForbiddenException;

/** Tests unitaires de la logique d'autorisation propriétaire/admin (Phase 1.2). */
class AuthUtilsTest {

    private Authentication auth(String userId, String role) {
        return new UsernamePasswordAuthenticationToken(userId, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    @Test
    void le_proprietaire_a_acces_a_sa_ressource() {
        assertThatCode(() -> AuthUtils.ensureOwnerOrAdmin(1L, auth("1", "CLIENT")))
                .doesNotThrowAnyException();
    }

    @Test
    void l_admin_a_acces_a_toute_ressource() {
        assertThatCode(() -> AuthUtils.ensureOwnerOrAdmin(999L, auth("1", "ADMIN")))
                .doesNotThrowAnyException();
    }

    @Test
    void un_non_proprietaire_non_admin_est_refuse() {
        assertThatThrownBy(() -> AuthUtils.ensureOwnerOrAdmin(2L, auth("1", "CLIENT")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void isAdmin_distingue_les_roles() {
        org.assertj.core.api.Assertions.assertThat(AuthUtils.isAdmin(auth("1", "ADMIN"))).isTrue();
        org.assertj.core.api.Assertions.assertThat(AuthUtils.isAdmin(auth("1", "CLIENT"))).isFalse();
    }
}
