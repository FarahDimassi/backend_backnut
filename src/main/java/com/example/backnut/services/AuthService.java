package com.example.backnut.services;

import com.example.backnut.models.User;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.security.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
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
    // Flux de réinitialisation : génère et envoie un OTP à l'email
    public String forgotPassword(String email) {
        // 1. Vérifier si l'utilisateur existe via l'email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Aucun utilisateur trouvé avec cet email : " + email);
        }

        User user = userOpt.get();

        // 2. Générer un code OTP à 4 chiffres
        String otp = generateOtp(4);

        // 3. Définir l'expiration dans 5 minutes
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        user.setResetOtp(otp);
        user.setResetOtpExpiry(expiryTime);
        userRepository.save(user);

        // 4. Envoyer l'OTP par email
        sendOtpByEmail(email, otp);

        return "Un OTP a été envoyé à votre adresse email.";
    }

    // Génération d'un OTP numérique
    private String generateOtp(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10);
            sb.append(digit);
        }
        return sb.toString();
    }

    // Envoi de l'OTP par email avec gestion d'exception
    private void sendOtpByEmail(String toEmail, String otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Code de réinitialisation de mot de passe");
        mailMessage.setText("Bonjour,\n\nVoici votre code de réinitialisation : " + otp
                + "\nIl expire dans 5 minutes.\n\nCordialement,\nVotre application");
        try {
            mailSender.send(mailMessage);
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", ex);
        }
    }

    // Vérification de l'OTP et réinitialisation du mot de passe
    public String verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        // 1. Vérifier si l'utilisateur existe
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Aucun utilisateur trouvé avec cet email : " + email);
        }

        User user = userOpt.get();

        // 2. Vérifier que l'OTP correspond et n'est pas expiré
        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new RuntimeException("OTP invalide.");
        }

        if (user.getResetOtpExpiry() == null || LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            throw new RuntimeException("OTP expiré, veuillez en générer un nouveau.");
        }

        // 3. Réinitialiser le mot de passe en l'encodeant
        user.setPassword(passwordEncoder.encode(newPassword));
        // Effacer l'OTP et sa date d'expiration
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);

        userRepository.save(user);

        return "Mot de passe réinitialisé avec succès.";
    }

}
