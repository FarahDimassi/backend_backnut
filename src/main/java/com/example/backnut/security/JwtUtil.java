package com.example.backnut.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generateToken(Long id, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);  // Ajoute l'ID utilisateur
        claims.put("role", "ROLE_" + role); // Ajoute le rôle
        System.out.println("here1");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10h d'expiration
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        return getClaims(token).get("id", Long.class);
    }

    public Claims getClaims(String token) {
        try {
            if (token == null || token.trim().isEmpty() || token.trim().equalsIgnoreCase("null")) {
                throw new RuntimeException("Token JWT est vide");
            }
            // Si le token commence par "Bearer ", on le retire.
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            System.out.println("🔍 Token après correction : " + token);
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du décodage du JWT : " + e.getMessage());
            throw new RuntimeException("Token JWT invalide !");
        }
    }
}
