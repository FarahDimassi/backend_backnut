package com.example.backnut.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class JwtSecretConfiguration {

    @Bean
    public String jwtSecret(Environment env) {
        String secret = env.getProperty("jwt.secret");
        try {
            // On tente de décoder la chaîne. Si c'est déjà du Base64 valide, aucune exception n'est levée.
            Base64.getDecoder().decode(secret);
            return secret; // Déjà en Base64 valide
        } catch (IllegalArgumentException e) {
            // Sinon, on encode la valeur en Base64 et on la retourne.
            return Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
