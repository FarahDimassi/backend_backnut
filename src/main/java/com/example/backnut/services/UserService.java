package com.example.backnut.services;

import com.example.backnut.models.User;
import com.example.backnut.models.Notification;
import com.example.backnut.models.Invitation;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.repository.NotificationRepository;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private InvitationRepository invitationRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    public UserService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void setJwtUtil(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;

    }

    public Optional<User> getUserById(Long id) {
       Optional<User> user= userRepository.findById(id);
       if(user.isEmpty()){
           return null;
       }
        return user;
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(notificationRepository::findByUser).orElse(null);
    }

    public User updateUser(Long userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());


            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            if (updatedUser.getPhotoUrl() != null && !updatedUser.getPhotoUrl().isEmpty()) {
                user.setPhotoUrl(updatedUser.getPhotoUrl());
            }
            userRepository.save(user);
            String newToken = jwtUtil.generateToken(user.getId(),  user.getRole());
            System.out.println("✅ Nouveau Token : " + newToken);
            return user;
        } else {
            throw new RuntimeException("Utilisateur non trouvé !");
        }
    }

    public List<User> getAllActiveCoaches() {
        return userRepository.findAll().stream()
                .filter(user -> "Coach".equalsIgnoreCase(user.getRole()) && user.isActive())
                .collect(Collectors.toList());
    }

    public Invitation sendInvitation(Long userId, Long coachId) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        User receiver = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (!"Coach".equalsIgnoreCase(receiver.getRole())) {
            throw new RuntimeException("Le destinataire n'est pas un coach.");
        }

        Invitation invitation = new Invitation();
        invitation.setSender(sender);
        invitation.setReceiver(receiver);
        invitation.setStatus("PENDING");
        return invitationRepository.save(invitation);
    }

    public String uploadUserPhoto(Long userId, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Le fichier est vide");
        }

        // Récupérer l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé pour id : " + userId));

        // Nettoyer le nom du fichier et générer un nom unique
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = userId + "_" + System.currentTimeMillis() + "_" + originalFilename;

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

    // Méthode pour récupérer un coach par son identifiant
    public User getCoachById(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach non trouvé"));
        if (!"Coach".equalsIgnoreCase(coach.getRole())) {
            throw new RuntimeException("L'utilisateur avec l'id " + coachId + " n'est pas un coach");
        }
        return coach;
    }


}
