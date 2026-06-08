package com.example.restservice.shared.security;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Limite les tentatives sur les endpoints d'authentification (anti brute-force).
 * Fenêtre fixe par IP, en mémoire : suffisant pour une instance unique.
 * (Pour un déploiement multi-instances, déporter le compteur dans Redis.)
 *
 * <p>Volontairement instancié manuellement dans {@code SecurityConfig} plutôt que déclaré
 * en {@code @Component}, pour éviter l'auto-enregistrement servlet sur TOUTES les requêtes.
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000L;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    private static final class Window {
        long start;
        int count;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        return !("/auth/login".equals(uri) || "/auth/register".equals(uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!allow(clientKey(request))) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\","
                    + "\"message\":\"Trop de tentatives. Réessayez dans une minute.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /** Incrémente le compteur de la fenêtre courante et indique si la requête est autorisée. */
    private boolean allow(String key) {
        long now = System.currentTimeMillis();
        Window window = windows.compute(key, (k, existing) -> {
            Window w = existing;
            if (w == null || now - w.start >= WINDOW_MS) {
                w = new Window();
                w.start = now;
            }
            w.count++;
            return w;
        });
        return window.count <= MAX_REQUESTS;
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
