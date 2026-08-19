package com.shiba.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    private final SecretKey key;
    private final Long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String userId, String username, String role) {
        return Jwts.builder()
                .subject(userId)
                .claims(Map.of(
                        "username", username,
                        "role", role
                ))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }
}
