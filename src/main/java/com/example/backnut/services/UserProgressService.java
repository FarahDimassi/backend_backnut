package com.example.backnut.services;

import com.example.backnut.models.UserProgress;
import com.example.backnut.repository.UserProgressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserProgressService {

    @Autowired
    private UserProgressRepository progressRepo;

    @Transactional
    public UserProgress saveProgress(UserProgress progress) {
        // Sauvegarde directe de l’entité reçue
        return progressRepo.save(progress);
    }
    public double computeProgressPercentage(UserProgress p) {
        double init = p.getInitialWeight();
        double curr = p.getCurrentWeight();
        double target = p.getTargetWeight();
        if (init == target) return 100.0;
        return ((init - curr) / (init - target)) * 100.0;
    }

    /**
     * Récupère la dernière progression d'un user et renvoie son %.
     */
    public Optional<Double> getUserProgress(Long userId) {
        return progressRepo.findAll().stream()
                .filter(p -> Objects.equals(p.getUserId(), userId))
                .max(Comparator.comparing(UserProgress::getDate))
                .map(this::computeProgressPercentage);
    }

    /**
     * Génère les données de chart pour admin :
     * - une ligne “reel”, une ligne “ia”
     * - sur l'ensemble des dates de soumission
     */
    public ChartData getProgressChartData() {
        List<UserProgress> all = progressRepo.findAll();

        // regrouper par coachType puis par date → liste de pourcentages
        Map<String, Map<LocalDate, List<Double>>> grouped = all.stream()
                .collect(Collectors.groupingBy(
                        UserProgress::getCoachType,
                        Collectors.groupingBy(
                                UserProgress::getDate,
                                Collectors.mapping(this::computeProgressPercentage, Collectors.toList())
                        )
                ));

        // obtenir l'ensemble trié des dates présentes
        SortedSet<LocalDate> dates = new TreeSet<>();
        grouped.values().forEach(m -> dates.addAll(m.keySet()));

        // pour chaque coachType, construire la série moyenne
        List<Double> reelSeries = new ArrayList<>();
        List<Double> iaSeries   = new ArrayList<>();
        List<String> xAxis      = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        for (LocalDate d : dates) {
            // moyenne pour “reel”
            List<Double> lReel = grouped.getOrDefault("reel", Collections.emptyMap())
                    .getOrDefault(d, Collections.emptyList());
            double avgReel = lReel.isEmpty() ? 0.0
                    : lReel.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            reelSeries.add(avgReel);

            // moyenne pour “ia”
            List<Double> lIa = grouped.getOrDefault("ia", Collections.emptyMap())
                    .getOrDefault(d, Collections.emptyList());
            double avgIa = lIa.isEmpty() ? 0.0
                    : lIa.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            iaSeries.add(avgIa);
        }

        return new ChartData(xAxis, reelSeries, iaSeries);
    }

    /** DTO interne pour chart data */
    public static class ChartData {
        private List<String> dates;
        private List<Double> reel;
        private List<Double> ia;

        public ChartData(List<String> dates, List<Double> reel, List<Double> ia) {
            this.dates = dates;
            this.reel = reel;
            this.ia = ia;
        }
        public List<String> getDates() { return dates; }
        public List<Double> getReel()   { return reel; }
        public List<Double> getIa()     { return ia; }
    }
}