package com.example.backnut.services;

import com.example.backnut.models.Invitation;
import com.example.backnut.models.User;
import com.example.backnut.models.UserProgress;
import com.example.backnut.repository.InvitationRepository;
import com.example.backnut.repository.UserProgressRepository;
import com.example.backnut.repository.UserRepository;
import com.example.backnut.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final InvitationRepository invitationRepository;
    private final UserProgressRepository userProgressRepository;



    public AdminService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService,
                        InvitationRepository invitationRepository,
                        JwtUtil jwtUtil,
                        UserProgressRepository userProgressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.invitationRepository = invitationRepository;
        this.jwtUtil = jwtUtil;
        this.userProgressRepository=userProgressRepository;
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
    private double computeProgressPercent(UserProgress p) {
        double init   = p.getInitialWeight();
        double curr   = p.getCurrentWeight();
        double target = p.getTargetWeight();
        if (init == target) return 100.0;
        return ((init - curr) / (init - target)) * 100.0;
    }

    /** Renvoie la liste brute de toutes les saisies de progression */
    public List<UserProgress> getAllProgressEntries() {
        return userProgressRepository.findAll();
    }

    public Map<String, Object> getUserProgressSummary(Long userId) {
        Optional<UserProgress> opt = userProgressRepository.findAll().stream()
                .filter(p -> Objects.equals(p.getUserId(), userId))
                .max(Comparator.comparing(UserProgress::getDate));

        if (opt.isEmpty()) {
            return null;
        }

        UserProgress p = opt.get();
        double percent = computeProgressPercent(p);

        Map<String,Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("coachType", p.getCoachType());
        summary.put("bmi", p.getBmi());
        summary.put("avis", p.getFeedback());
        summary.put("progressPercent", percent);
        return summary;
    }
    /**
     * Construit les données de courbe pour l’admin :
     * - xAxis = dates de soumission
     * - serie “reel” = moyenne des % pour chaque date
     * - serie “ia”   = moyenne des % pour chaque date
     */
    public List<Map<String,Object>> getAllUsersProgressSummary() {
        List<Long> userIds = userProgressRepository.findDistinctUserIds();
        List<Map<String,Object>> summaries = new ArrayList<>();

        for (Long uid : userIds) {
            Optional<UserProgress> opt = userProgressRepository
                    .findTopByUserIdOrderByDateDesc(uid);
            if (opt.isPresent()) {
                UserProgress p = opt.get();
                double percent = computeProgressPercent(p);

                Map<String,Object> summary = new HashMap<>();
                summary.put("userId", uid);
                summary.put("coachType", p.getCoachType());
                summary.put("bmi", p.getBmi());
                summary.put("avis", p.getFeedback());
                summary.put("progressPercent", percent);

                summaries.add(summary);
            }
        }
        return summaries;
    }
    public ChartData buildProgressChart() {
        List<UserProgress> all = userProgressRepository.findAll();

        // Grouper par coachType puis par date
        Map<String, Map<LocalDate, List<Double>>> grouped = all.stream()
                .collect(Collectors.groupingBy(
                        UserProgress::getCoachType,
                        Collectors.groupingBy(
                                UserProgress::getDate,
                                Collectors.mapping(this::computeProgressPercent, Collectors.toList())
                        )
                ));

        // Toutes les dates triées
        SortedSet<LocalDate> dates = new TreeSet<>();
        grouped.values().forEach(m -> dates.addAll(m.keySet()));

        List<String> xAxis = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        List<Double> reelSeries = new ArrayList<>();
        List<Double> iaSeries   = new ArrayList<>();

        for (LocalDate d : dates) {
            List<Double> r = grouped.getOrDefault("reel", Collections.emptyMap())
                    .getOrDefault(d, Collections.emptyList());
            reelSeries.add(r.isEmpty() ? 0.0 : r.stream().mapToDouble(Double::doubleValue).average().orElse(0));

            List<Double> i = grouped.getOrDefault("ia", Collections.emptyMap())
                    .getOrDefault(d, Collections.emptyList());
            iaSeries.add(i.isEmpty() ? 0.0 : i.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        }

        return new ChartData(xAxis, reelSeries, iaSeries);
    }

    /** Simple holder des données de chart */
    public static class ChartData {
        private List<String> dates;
        private List<Double> reel;
        private List<Double> ia;

        public ChartData(List<String> dates, List<Double> reel, List<Double> ia) {
            this.dates = dates;
            this.reel  = reel;
            this.ia    = ia;
        }
        public List<String> getDates()        { return dates; }
        public List<Double> getReel()         { return reel; }
        public List<Double> getIa()           { return ia; }
    }
}
