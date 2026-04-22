package com.example.restservice.service.user;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.restservice.auth.AppJwtProperties;
import com.example.restservice.auth.RefreshToken;
import com.example.restservice.model.user.Utilisateur;
import com.example.restservice.repository.user.RefreshTokenRepository;


@Service
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppJwtProperties props;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenService(RefreshTokenRepository refreshTokenRepository, AppJwtProperties props) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.props = props;
    }

    public String createRefreshTokenPlain(Utilisateur user){
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String tokenPlain = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String hash = sha256(tokenPlain);
        RefreshToken r = new RefreshToken();
        r.setTokenHash(hash);
        r.setUtilisateur(user);
        r.setExpireAt(Instant.now().plus(props.getRefreshExpirationDays(), ChronoUnit.DAYS));
        r.setRevoked(false);
        refreshTokenRepository.save(r);
        return tokenPlain;
    }

    public Optional<RefreshToken> findByTokenPlain(String tokenPlain) {
        String hash = sha256(tokenPlain);
        return refreshTokenRepository.findByTokenHash(hash);
    }

    public void revoke(RefreshToken token, String replacedBy) {
        token.setRevoked(true);
        token.setReplacedBy(replacedBy);
        refreshTokenRepository.save(token);
    }

    public String rotateRefreshToken(RefreshToken current, Utilisateur user) {
        String newPlain = createRefreshTokenPlain(user);
        revoke(current, sha256(newPlain));
        return newPlain;
    }

    public String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error computing SHA-256 hash", e);
        }
    }

}
