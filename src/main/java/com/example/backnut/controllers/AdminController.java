package com.example.backnut.controllers;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final InvitationRepository invitationRepository;

    public AdminController(AdminService adminService , InvitationRepository invitationRepository) {
        this.adminService = adminService;
        this.invitationRepository=invitationRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(adminService.createUser(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(adminService.updateUser(id, userDetails));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().body(Map.of("message", "Utilisateur supprimé avec succès !"));
    }

    // ✅ Nouveau endpoint : extraction de l’email à partir du token du coach
    @GetMapping("/extract-email")
    public ResponseEntity<?> extractEmailFromToken(@RequestParam("token") String token) {
        String email = adminService.extractEmailFromToken(token);
        if(email.isEmpty() ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");

        }
        return ResponseEntity.ok(Map.of("email", email));
    }
    @PostMapping("/accept-reset")
    public ResponseEntity<?> acceptReset(@RequestParam Long invitationId) {
        Invitation invitation = adminService.acceptResetRequest(invitationId);
        return ResponseEntity.ok(Map.of(
                "message", "Demande de réinitialisation acceptée. L'utilisateur peut désormais envoyer une nouvelle invitation.",
                "invitationId", invitation.getId()
        ));
    }
    @GetMapping("/reset-requests")
    public ResponseEntity<List<Invitation>> getResetRequests() {
        List<Invitation> requests = invitationRepository.findAll().stream()
                .filter(inv -> "ADMIN_REQUESTED".equalsIgnoreCase(inv.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

}
