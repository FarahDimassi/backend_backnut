package com.example.backnut.controllers;
import com.example.backnut.models.UserProgress;
import com.example.backnut.services.UserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class UserProgressController {

    @Autowired
    private UserProgressService progressService;

    @PostMapping
    public ResponseEntity<UserProgress> submitProgress(@RequestBody UserProgress progress) {
        UserProgress saved = progressService.saveProgress(progress);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Double> getUserProgress(@PathVariable Long userId) {
        Optional<Double> percent = progressService.getUserProgress(userId);
        return percent
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

}
