package com.example.backnut.services;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.Notification;
import com.example.backnut.models.User;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.repository.NotificationRepository;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import java.util.List;

@Service
public class CoachService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CoachService(InvitationRepository invitationRepository,
                        UserRepository userRepository,
                        NotificationRepository notificationRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<Invitation> getInvitationsForCoach(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));
        return invitationRepository.findByReceiver(coach);
    }

    public Invitation acceptInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation introuvable"));
        invitation.setStatus("ACCEPTED");
        return invitationRepository.save(invitation);
    }

    public boolean canChat(Long userId, Long coachId) {
        User user = userRepository.findById(userId).orElseThrow();
        User coach = userRepository.findById(coachId).orElseThrow();
        Invitation invitation = invitationRepository.findBySenderAndReceiver(user, coach);
        return invitation != null && "ACCEPTED".equals(invitation.getStatus());
    }

    public String getDashboardMessage() {
        return "Bienvenue sur le tableau de bord Coach 👋";
    }

    public Optional<User> getCoachById(Long coachId) {
        return userRepository.findById(coachId);
    }

    // 🖊 Récupérer les notifications du coach
    public List<Notification> getNotificationsByCoachId(Long coachId) {
        Optional<User> coach = userRepository.findById(coachId);
        return coach.map(notificationRepository::findByUser).orElse(null);
    }

    public User updateCoach(Long coachId, User updatedCoach) {
        Optional<User> existingUser = userRepository.findById(coachId);

        if (existingUser.isPresent()) {
            User coach = existingUser.get();
            coach.setUsername(updatedCoach.getUsername());
            coach.setEmail(updatedCoach.getEmail());

            if (updatedCoach.getPassword() != null && !updatedCoach.getPassword().isEmpty()) {
                coach.setPassword(passwordEncoder.encode(updatedCoach.getPassword()));
            }
            if (updatedCoach.getPhotoUrl() != null && !updatedCoach.getPhotoUrl().isEmpty()) {
                coach.setPhotoUrl(updatedCoach.getPhotoUrl());
            }
            userRepository.save(coach);

            String newToken = jwtUtil.generateToken(coach.getId(), coach.getRole());
            System.out.println("✅ Nouveau Token : " + newToken);
            return coach;
        } else {
            throw new RuntimeException("Utilisateur non trouvé !");
        }
    }

    public String uploadCoachPhoto(Long coachId, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Le fichier est vide");
        }

        // Récupérer l'utilisateur
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé pour id : " + coachId));

        // Nettoyer le nom du fichier et générer un nom unique
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = coachId + "_" + System.currentTimeMillis() + "_" + originalFilename;

        // Dossier de stockage (ici "uploads" à la racine du projet)
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copier le fichier dans le dossier uploads
        Path filePath = uploadPath.resolve(filename);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'upload du fichier", e);
        }

        // Optionnel : vous pouvez mettre à jour l'utilisateur avec l'URL complète,
        // mais ici on renvoie juste le nom du fichier.
        return filename;
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le username : " + username));
    }
}
