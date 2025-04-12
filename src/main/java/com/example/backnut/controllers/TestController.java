package com.example.backnut.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/protected")
    public String protectedRoute() {
        return "✅ Accès utilisateur  autorisé à /api/protected !";
    }

    @GetMapping("/admin")
    public String adminRoute() {
        return "🚀 Accès ADMIN autorisé à /api/admin !";
    }
}
