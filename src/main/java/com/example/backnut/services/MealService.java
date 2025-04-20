package com.example.backnut.services;

import com.example.backnut.models.CalendarPlan;
import com.example.backnut.models.Meal;
import com.example.backnut.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    // Ajouter un enregistrement regroupant tous les repas d'une journée
    public Meal addMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    // Mise à jour complète (PUT) – méthode existante
    public Meal updateMeal(Long id, Meal updatedMeal) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id " + id));
        meal.setCoachId(updatedMeal.getCoachId());
        meal.setUserId(updatedMeal.getUserId());
        meal.setBreakfast(updatedMeal.getBreakfast());
        meal.setLunch(updatedMeal.getLunch());
        meal.setDinner(updatedMeal.getDinner());
        meal.setSnacks(updatedMeal.getSnacks());
        meal.setSport(updatedMeal.getSport());
        meal.setWater(updatedMeal.getWater());
        meal.setDate(updatedMeal.getDate());
        return mealRepository.save(meal);
    }

    // Mise à jour partielle (PATCH) : ne modifier que les champs non nuls de updatedMeal
    public Meal patchMeal(Long id, Meal updatedMeal) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id " + id));

        if (updatedMeal.getCoachId() != null) {
            meal.setCoachId(updatedMeal.getCoachId());
        }
        if (updatedMeal.getUserId() != null) {
            meal.setUserId(updatedMeal.getUserId());
        }
        if (updatedMeal.getBreakfast() != null) {
            meal.setBreakfast(updatedMeal.getBreakfast());
        }
        if (updatedMeal.getLunch() != null) {
            meal.setLunch(updatedMeal.getLunch());
        }
        if (updatedMeal.getDinner() != null) {
            meal.setDinner(updatedMeal.getDinner());
        }
        if (updatedMeal.getSnacks() != null) {
            meal.setSnacks(updatedMeal.getSnacks());
        }
        if (updatedMeal.getSport() != null) {
            meal.setSport(updatedMeal.getSport());
        }
        if (updatedMeal.getWater() != null) {
            meal.setWater(updatedMeal.getWater());
        }
        if (updatedMeal.getDate() != null) {
            meal.setDate(updatedMeal.getDate());
        }
        return mealRepository.save(meal);
    }

    // Supprimer un enregistrement
    public void deleteMeal(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id " + id));
        mealRepository.delete(meal);
    }

    // Récupérer tous les enregistrements pour un utilisateur
    public List<Meal> getMealsForUser(Long userId) {
        return mealRepository.findByUserId(userId);
    }

    // Optionnel : retourner la liste par défaut des types de repas
    public List<String> getDefaultMealTypes() {
        return Arrays.asList("Breakfast", "Lunch", "Dinner", "Snacks", "Sport", "Eau");
    }

    // Retourner un repas unique pour une date donnée, un utilisateur et un coach
    public Meal getMealByDateAndUserIdAndCoachId(LocalDate date, Long userId, Long coachId) {
        return mealRepository.findOneByDateAndUserIdAndCoachId(date, userId, coachId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun plan trouvé pour la date " + date +
                                ", userId " + userId +
                                " et coachId " + coachId));
    }

    // Récupère les repas pour un utilisateur et une date
    public List<Meal> getMealsByUserAndDate(Long userId, LocalDate date) {
        return mealRepository.findByUserIdAndDate(userId, date);
    }

    // Nouvelle méthode : récupérer les repas pour un utilisateur, un coach et une date
    public List<Meal> getMealsByUserAndCoachAndDate(Long userId, Long coachId, LocalDate date) {
        List<Meal> meals = mealRepository.findByUserIdAndCoachIdAndDate(userId, coachId, date);
        if (meals == null || meals.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Aucun plan trouvé pour la date " + date +
                            ", userId " + userId +
                            " et coachId " + coachId);
        }
        return meals;
    }
    public List<Meal> getPlansForUserAndDate(Long userId, LocalDate date) {
        return mealRepository.findByUserIdAndDate(userId, date);
    }
    /**
     * Met à jour l'état "tick" d'un champ de Meal
     * @param id     l'identifiant du Meal
     * @param field  le nom du champ à cocher (breakfast, lunch, etc.)
     * @param status true = coché, false = décoché
     */
    public Meal patchMealTick(Long id, String field, Boolean status) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id " + id));

        switch (field) {
            case "breakfast": meal.setBreakfastTick(status); break;
            case "lunch":     meal.setLunchTick(status);     break;
            case "dinner":    meal.setDinnerTick(status);    break;
            case "snacks":    meal.setSnacksTick(status);    break;
            case "sport":     meal.setSportTick(status);     break;
            case "water":     meal.setWaterTick(status);     break;
            default:
                throw new IllegalArgumentException("Unknown tick field: " + field);
        }

        return mealRepository.save(meal);
    }
    public List<Meal> findByCoachAndDate(Long coachId, LocalDate date) {
        return mealRepository.findByCoachIdAndDate(coachId, date);
    }
}
