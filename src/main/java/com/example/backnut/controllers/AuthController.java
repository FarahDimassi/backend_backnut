package com.example.backnut.controllers;

import com.example.backnut.models.User;
import com.example.backnut.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String result = authService.register(user);

            // Vérifie si ce n’est pas un vrai token (car le message pour Coach est textuel)
            if (result.startsWith("eyJ")) {
                return ResponseEntity.ok().body(Map.of("token", result));
            } else {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", result));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("testing test");
        try {
            String token = authService.login(credentials.get("username"), credentials.get("password"));
            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        // Vérifier que l'en-tête Authorization est présent et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("En-tête d'autorisation manquant ou invalide.");
        }
        // Extraire le token en retirant le préfixe "Bearer "
        String token = authHeader.substring(7);
        // Appeler la méthode logout du service AuthService
        String message = authService.logout(token);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String message = authService.forgotPassword(email);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndResetPassword(@RequestParam String email,
                                                            @RequestParam String otp,
                                                            @RequestParam String newPassword) {
        String message = authService.verifyOtpAndResetPassword(email, otp, newPassword);
        return ResponseEntity.ok(message);
    }
}
