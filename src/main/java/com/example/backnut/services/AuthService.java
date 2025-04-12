package com.example.backnut.services;

import com.example.backnut.models.User;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris.");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("L'email est obligatoire.");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new RuntimeException("Le rôle est obligatoire (User ou Coach).");
        }

        String role = user.getRole().trim();
        if (!role.equalsIgnoreCase("User") && !role.equalsIgnoreCase("Coach")) {
            throw new RuntimeException("Rôle invalide. Seuls 'User' ou 'Coach' sont acceptés.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (role.equalsIgnoreCase("Coach")) {
            user.setRole("Coach");
            user.setActive(false); // Doit être validé par admin
        } else {
            user.setRole("User");
            user.setActive(true); // Utilisateur actif immédiatement
        }

        User savedUser = userRepository.save(user);

        if (savedUser.getRole().equals("Coach")) {
            String token = jwtUtil.generateToken(
                    savedUser.getId(),
                    savedUser.getRole()
            );
            System.out.println("📨 Token coach (à donner à l'admin) : " + token);
            return "Inscription en tant que coach reçue. Token : " + token;
        }

        return jwtUtil.generateToken(savedUser.getId(),  savedUser.getRole());
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Identifiants invalides.");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            throw new RuntimeException("Votre compte n'est pas encore activé.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect.");
        }

        return jwtUtil.generateToken(user.getId(),  user.getRole());
    }

    public void activateCoach(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!coach.getRole().equalsIgnoreCase("Coach")) {
            throw new RuntimeException("Cet utilisateur n'est pas un coach.");
        }

        coach.setActive(true);
        userRepository.save(coach);

        // TODO: Envoyer email de confirmation d'activation
    }
    public String logout(String token) {
        // Dans une architecture stateless, logout côté serveur n'invalide pas réellement le token
        // sauf si vous gérez une blacklist de tokens.
        // Ici, vous pouvez ajouter le token à une blacklist ou simplement retourner un message.
        // Exemple sans gestion de blacklist :
        return "Déconnexion réussie.";

        // Exemple si vous utilisez une blacklist (à adapter) :
        // tokenBlacklistService.addTokenToBlacklist(token);
        // return "Déconnexion réussie, token invalidé.";
    }
}
