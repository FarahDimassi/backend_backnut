package com.example.backnut.services;

import com.example.backnut.models.CalendarPlan;
import com.example.backnut.repository.CalendarPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarPlanService {

    @Autowired
    private CalendarPlanRepository calendarPlanRepository;

    // Créer ou mettre à jour un plan
    public CalendarPlan createOrUpdatePlan(CalendarPlan plan) {
        return calendarPlanRepository.save(plan);
    }

    // Récupérer tous les plans pour un utilisateur et un coach
    public List<CalendarPlan> getPlansForUserAndCoach(Long userId, Long coachId) {
        return calendarPlanRepository.findByUserIdAndCoachId(userId, coachId);
    }

    // Récupérer les plans pour un utilisateur, un coach et une date spécifique
    public List<CalendarPlan> getPlansForUserAndCoachAndDate(Long userId, Long coachId, LocalDate date) {
        return calendarPlanRepository.findByUserIdAndCoachIdAndDate(userId, coachId, date);
    }

    // Créer ou mettre à jour un plan dans la table calendar_plan.
    // Si un plan existe déjà pour (userId, coachId, date), on le met à jour ; sinon, on en crée un nouveau.
    public CalendarPlan addOrUpdateCalendarPlan(Long userId, Long coachId, LocalDate date, String username, String remarque) {
        Optional<CalendarPlan> optionalPlan = calendarPlanRepository.findOneByDateAndUserIdAndCoachId(date, userId, coachId);
        CalendarPlan plan;
        if (optionalPlan.isPresent()) {
            plan = optionalPlan.get();

            plan.setRemarque(remarque);
        } else {
            plan = new CalendarPlan(userId, coachId, date, remarque);
        }
        return calendarPlanRepository.save(plan);
    }
    public List<CalendarPlan> getPlansForCoachAndDate(Long coachId, LocalDate date) {
        return calendarPlanRepository.findByCoachIdAndDate(coachId, date);
    }
    public void deleteRemark(Long id) {
        // Supposons ici que la remarque est une partie de l'entité CalendarPlan ou une entité à part entière.
        // Si c'est une entité à part, ajustez en fonction de votre repository.
        calendarPlanRepository.deleteById(id);
    }
    public List<CalendarPlan> getPlansForUserAndDate(Long userId, LocalDate date) {
        return calendarPlanRepository.findByUserIdAndDate(userId, date);
    }

}
