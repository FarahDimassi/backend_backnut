package com.example.backnut.controllers;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.models.Notification;
import com.example.backnut.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "*") // Permet les requêtes depuis n'importe quelle origine (utile pour React Native)
public class UserController {

    @Autowired
    private UserService userService;

    // 📌 Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 📌 Récupérer les notifications d'un utilisateur
    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long id) {
        List<Notification> notifications = userService.getNotificationsByUserId(id);
        return ResponseEntity.ok(notifications);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/coaches")
    public List<User> getAllCoaches() {
        return userService.getAllActiveCoaches();
    }
    @GetMapping("/coaches/{coachId}")
    public ResponseEntity<?> getCoachById(@PathVariable Long coachId) {
        try {
            User coach = userService.getCoachById(coachId);
            return ResponseEntity.ok(coach);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 🔹 Envoyer une invitation à un coach
    @PostMapping("/invite")
    public ResponseEntity<?> inviteCoach(@RequestParam Long userId, @RequestParam Long coachId) {
        Invitation invitation = userService.sendInvitation(userId, coachId);
        return ResponseEntity.ok(Map.of("message", "Invitation envoyée !", "invitationId", invitation.getId()));
    }
    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String filename = userService.uploadUserPhoto(userId, file);
            // Ici, le service doit renvoyer la photoUrl complète si possible.
            return ResponseEntity.ok(Map.of("message", "Photo uploadée avec succès", "photoUrl", "http://localhost:8080/uploads/" + filename));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de l'upload de la photo : " + e.getMessage());
        }
    }



}
