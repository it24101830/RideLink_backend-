package com.ridelink.account.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(String userId, String role, String email) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
            .subject(userId)
            .claim("role", role)
            .claim("email", email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
    }

    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload();
    }
}
