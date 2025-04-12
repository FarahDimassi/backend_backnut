package com.example.backnut.repository;

import com.example.backnut.models.CalendarPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarPlanRepository extends JpaRepository<CalendarPlan, Long> {

    // Récupérer tous les plans pour un utilisateur et un coach
    List<CalendarPlan> findByUserIdAndCoachId(Long userId, Long coachId);

    // Récupérer les plans pour un utilisateur, un coach et une date donnée
    List<CalendarPlan> findByUserIdAndCoachIdAndDate(Long userId, Long coachId, LocalDate date);

    // Récupérer un plan unique par date, user et coach (pour vérifier l'existence d'un plan)
    Optional<CalendarPlan> findOneByDateAndUserIdAndCoachId(LocalDate date, Long userId, Long coachId);
    List<CalendarPlan> findByCoachIdAndDate(Long coachId, LocalDate date);
    List<CalendarPlan> findByUserIdAndDate(Long userId, LocalDate date);

}
