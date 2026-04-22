package com.example.restservice.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenMillis;

    public JwtUtil(AppJwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes());
        this.accessTokenMillis = props.getAccessExpirationMinutes() * 60L * 1000L;
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(subject)
            .addClaims(claims)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + accessTokenMillis))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> validate(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public boolean isExpired(Date expiration) {
        return expiration.before(new Date());
    }
}
