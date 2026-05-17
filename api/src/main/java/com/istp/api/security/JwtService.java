package com.istp.api.security;

import com.istp.api.common.UserRole;
import com.istp.api.service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final String secret;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expirationMillis
    ) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(User user) {
        return generateToken(user.getId(), user.getEmail(), user.getRole(), false);
    }

    public String generateTestToken(User user) {
        return generateToken(user.getId(), user.getEmail(), user.getRole(), true);
    }

    private String generateToken(Long userId, String email, UserRole role, boolean testToken) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role.name())
                .claim("testToken", testToken)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey())
                .compact();
    }

    public String extractEmail(String token) {
        return claims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return claims(token).get("userId", Long.class);
    }

    public UserRole extractRole(String token) {
        return UserRole.valueOf(claims(token).get("role", String.class));
    }

    public boolean isTestToken(String token) {
        Boolean testToken = claims(token).get("testToken", Boolean.class);
        return Boolean.TRUE.equals(testToken);
    }

    public boolean isValid(String token, AppUserPrincipal user) {
        return user.getUsername().equals(extractEmail(token)) && claims(token).getExpiration().after(new Date());
    }

    public boolean isNotExpired(String token) {
        return claims(token).getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
