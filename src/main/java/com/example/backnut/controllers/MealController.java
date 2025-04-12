package com.example.backnut.controllers;

import com.example.backnut.models.Meal;
import com.example.backnut.services.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    // Ajouter une journée de repas
    @PostMapping
    public ResponseEntity<Meal> addMeal(@RequestBody Meal meal) {
        Meal createdMeal = mealService.addMeal(meal);
        return new ResponseEntity<>(createdMeal, HttpStatus.CREATED);
    }

    // Mise à jour complète d'un repas
    @PutMapping("/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id, @RequestBody Meal updatedMeal) {
        Meal meal = mealService.updateMeal(id, updatedMeal);
        return new ResponseEntity<>(meal, HttpStatus.OK);
    }

    // Mise à jour partielle d'un repas (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<Meal> patchMeal(@PathVariable Long id, @RequestBody Meal updatedMeal) {
        Meal meal = mealService.patchMeal(id, updatedMeal);
        return new ResponseEntity<>(meal, HttpStatus.OK);
    }

    // Supprimer un repas
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Récupérer tous les repas pour un utilisateur
    @GetMapping("/user/{userId}/coach/{coachId}")
    public ResponseEntity<List<Meal>> getMealsForUserAndCoachAndDate(
            @PathVariable Long userId,
            @PathVariable Long coachId,
            @RequestParam(required = false) String date
    ) {
        try {
            if (date == null) {
                // Si la date est obligatoire, on peut renvoyer une liste vide
                return new ResponseEntity<>(List.of(), HttpStatus.OK);
            }
            LocalDate localDate = LocalDate.parse(date);  // Format attendu : yyyy-MM-dd
            List<Meal> meals = mealService.getMealsByUserAndCoachAndDate(userId, coachId, localDate);
            return new ResponseEntity<>(meals, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Optionnel : retourner la liste par défaut des types de repas
    @GetMapping("/types")
    public ResponseEntity<List<String>> getDefaultMealTypes() {
        List<String> types = mealService.getDefaultMealTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }
    @GetMapping("/plan")
    public ResponseEntity<Meal> getMealPlan(
            @RequestParam String date,
            @RequestParam Long userId,
            @RequestParam Long coachId) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Meal meal = mealService.getMealByDateAndUserIdAndCoachId(localDate, userId, coachId);
            return new ResponseEntity<>(meal, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/plann")
    public ResponseEntity<Meal> getMealPlann(
            @RequestParam String date,
            @RequestParam Long userId,
            @RequestParam Long coachId) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Meal meal = mealService.getMealByDateAndUserIdAndCoachId(localDate, userId, coachId);
            return new ResponseEntity<>(meal, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/calend")
    public ResponseEntity<List<Meal>> getMealCalend(
            @RequestParam String date,
            @RequestParam Long userId) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            // Appel au service pour récupérer la liste des meals pour l'user et la date donnée
            List<Meal> meals = mealService.getPlansForUserAndDate(userId, localDate);
            return new ResponseEntity<>(meals, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            // En cas d'erreur de parsing de la date, on renvoie un bad request
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


}
