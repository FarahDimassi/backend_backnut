package com.example.backnut.repository;



import com.example.backnut.models.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    // Récupérer les plats ajoutés pour un utilisateur donné
    List<Meal> findByUserId(Long userId);
    Optional<Meal> findOneByDateAndUserIdAndCoachId(LocalDate date, Long userId, Long coachId);
    Optional<Meal> findOneByUserIdAndDate(Long userId, LocalDate date);
    List<Meal> findByUserIdAndDate(Long userId, LocalDate date);
    List<Meal> findByUserIdAndCoachIdAndDate(Long userId, Long coachId, LocalDate date);

}
