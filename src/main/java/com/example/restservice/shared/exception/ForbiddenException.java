package com.example.restservice.shared.exception;

/** Levée quand un utilisateur authentifié tente d'accéder à une ressource qui ne lui appartient pas. Traduite en HTTP 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
