package com.example.restservice.shared.exception;

/** Levée quand une ressource demandée n'existe pas. Traduite en HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
