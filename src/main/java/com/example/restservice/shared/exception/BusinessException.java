package com.example.restservice.shared.exception;

/** Levée pour une règle métier non respectée (ex : stock insuffisant). Traduite en HTTP 400. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
