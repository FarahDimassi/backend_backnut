package com.example.backnut.services;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final InvitationRepository invitationRepository;



    public AdminService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService,
                        InvitationRepository invitationRepository,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.invitationRepository = invitationRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));
    }

    public User createUser(User user) {
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(userDetails.getRole());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            if (!userDetails.getPassword().startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
        }

        if (existingUser.getRole().equalsIgnoreCase("Coach")
                && !existingUser.isActive()
                && userDetails.isActive()) {
            existingUser.setActive(true);
            emailService.sendActivationEmail(existingUser.getEmail(), existingUser.getUsername());
            System.out.println("📧 Email envoyé à " + existingUser.getEmail());
        } else {
            existingUser.setActive(userDetails.isActive());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ✅ Méthode ajoutée pour extraire l'email depuis le token
    public String extractEmailFromToken(String token) {
       Long userId= jwtUtil.extractUserId(token);
            Optional<User> user =userRepository.findById(userId);
            if(user.isPresent()){
                return user.get().getEmail();
            }
            return "";

    }
    public Invitation acceptResetRequest(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation introuvable"));
        if (!"ADMIN_REQUESTED".equalsIgnoreCase(invitation.getStatus())) {
            throw new RuntimeException("Cette invitation n'est pas en demande de réinitialisation.");
        }
        // Passage du statut à "RESET" pour débloquer l'envoi d'une nouvelle invitation
        invitation.setStatus("RESET");
        return invitationRepository.save(invitation);
    }

}
