package com.example.backnut.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
                                "http://*:8081",      // Pour Expo Web sur n'importe quelle IP
                                "http://*:19000",     // Pour Expo Go sur n'importe quelle IP
                                "exp://*:19000",      // Pour Expo Go
                                "exp://*:8081",       // Pour Expo Go alternatif
                                "http://*.*.*.*:*",   // Pour toute adresse IP en développement
                                "exp://*.*.*.*:*"     // Pour Expo Go sur toute adresse IP
                        )

                        .allowedMethods("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")  // Important pour les tokens JWT
                        .allowCredentials(true)
                        .maxAge(3600L);  // Cache la configuration CORS pendant 1 heure
            }
        };
    }
}