package com.example.restservice.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class AppJwtProperties {

    private String secret;
    private long accessExpirationMinutes;
    private long refreshExpirationDays;

    // GETTERS
    public String getSecret() {
        return secret;
    }

    public long getAccessExpirationMinutes() {
        return accessExpirationMinutes;
    }

    public long getRefreshExpirationDays() {
        return refreshExpirationDays;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccessExpirationMinutes(long accessExpirationMinutes) {
        this.accessExpirationMinutes = accessExpirationMinutes;
    }

    public void setRefreshExpirationDays(long refreshExpirationDays) {
        this.refreshExpirationDays = refreshExpirationDays;
    }
}
