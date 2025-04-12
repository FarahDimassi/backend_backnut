package com.example.backnut.controllers;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.Notification;
import com.example.backnut.models.User;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.services.CoachService;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.webauthn.management.MapPublicKeyCredentialUserEntityRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/coach")
@PreAuthorize("hasAuthority('ROLE_Coach')")
public class CoachController {

    private final CoachService coachService;
    private final UserRepository userRepository;

    public CoachController(CoachService coachService, UserRepository userRepository) {
        this.coachService = coachService;
        this.userRepository = userRepository;
    }
    @GetMapping("/dashboard")
    public String dashboard() {
        return coachService.getDashboardMessage();
    }

    // 🔹 Voir les invitations reçues
    @GetMapping("/{coachId}/invitations")
    public List<Invitation> getInvitations(@PathVariable Long coachId) {
        return coachService.getInvitationsForCoach(coachId);
    }

    // 🔹 Accepter une invitation
    @PostMapping("/accept/{invitationId}")
    public Invitation accept(@PathVariable Long invitationId) {
        return coachService.acceptInvitation(invitationId);
    }

    // 🔹 Vérifier si l'utilisateur peut discuter avec ce coach
    public Map<String, Boolean> canChat(Long coachId, Long targetUserId) {
        // Récupérer le coach
        Optional<User> coachOptional = userRepository.findById(coachId);
        if (!coachOptional.isPresent()) {
            throw new NoSuchElementException("Coach non trouvé pour l'ID: " + coachId);
        }
        // Récupérer l'utilisateur cible
        Optional<User> targetOptional = userRepository.findById(targetUserId);
        if (!targetOptional.isPresent()) {
            throw new NoSuchElementException("Utilisateur cible non trouvé pour l'ID: " + targetUserId);
        }

        // Logique de permission (ici, on autorise par défaut pour l'exemple)
        boolean allowed = true; // Remplacez par votre logique métier

        Map<String, Boolean> result = new HashMap<>();
        result.put("allowed", allowed);
        return result;
    }
    // 🔍 Récupérer les infos d'un coach
    @GetMapping("/{coachId}")
    public ResponseEntity<Optional<User>> getCoachById(@PathVariable Long coachId) {
        return ResponseEntity.ok(coachService.getCoachById(coachId));
    }

    // 🔔 Récupérer les notifications du coach
    @GetMapping("/{coachId}/notifications")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long coachId) {
        return ResponseEntity.ok(coachService.getNotificationsByCoachId(coachId));
    }

    // 🔄 Mise à jour du profil coach
  @GetMapping("/users/{id}")
 public ResponseEntity<User> getUserByIdForCoach(@PathVariable Long id) {
     Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isPresent()) {
  return ResponseEntity.ok(userOptional.get());
  } else {
  return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(null);

  }
}
    @PutMapping("/{coachId}")
    public ResponseEntity<?> updateCoach(@PathVariable Long coachId, @RequestBody User updatedCoach) {
        User coach = coachService.updateCoach(coachId, updatedCoach);

        if (coach != null) {
            return ResponseEntity.ok(coach);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 📌 Upload coach profile photo
    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("coachId") Long coachId,
            @RequestParam("file") MultipartFile file) {
        try {
            String filename = coachService.uploadCoachPhoto(coachId, file);
            // Ici, le service doit renvoyer la photoUrl complète si possible.
            return ResponseEntity.ok(Map.of("message", "Photo uploadée avec succès", "photoUrl", "http://localhost:8080/uploads/" + filename));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de l'upload de la photo : " + e.getMessage());
        }}
        @GetMapping
        public ResponseEntity<User> getUserByUsername(@RequestParam String username) {
            try {
                User user = coachService.getUserByUsername(username);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
}
