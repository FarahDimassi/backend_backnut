package com.example.backnut.services;

import com.example.backnut.models.Meal;
import com.example.backnut.models.Dish;
import com.example.backnut.repository.MealRepository;
import com.example.backnut.repository.ActivityRepository;
import com.example.backnut.repository.WaterIntakeRepository;
import com.example.backnut.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class StatsService {

    private final MealRepository        mealRepo;
    private final ActivityRepository    activityRepo;
    private final WaterIntakeRepository waterRepo;
    private final DishRepository        dishRepo;
    private final RestTemplate          rest = new RestTemplate();

    // Cache mémoire pour éviter de reconsulter OpenFoodFacts plusieurs fois
    private final Map<String, Integer> dishCaloriesCache = new ConcurrentHashMap<>();

    private static final Map<String,Integer> CALORIES_PER_MINUTE = Map.of(
            "course",   11,
            "natation", 8,
            "velo",     7,
            "marche",   4
    );
    private static final int RECOMMENDED_WATER_ML = 2000;

    public StatsService(MealRepository mealRepo,
                        ActivityRepository activityRepo,
                        WaterIntakeRepository waterRepo,
                        DishRepository dishRepo) {
        this.mealRepo     = mealRepo;
        this.activityRepo = activityRepo;
        this.waterRepo    = waterRepo;
        this.dishRepo     = dishRepo;
    }

    /**
     * 1) Stats journalières entre deux dates.
     *    → 1 seul findByUserId(), puis groupement en mémoire.
     */
    public List<Object[]> getDailyStats(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Meal> allMeals = mealRepo.findByUserId(userId);

        LinkedHashMap<LocalDate, StatsAccu> accu = new LinkedHashMap<>();
        LocalDate d = startDate;
        while (!d.isAfter(endDate)) {
            accu.put(d, new StatsAccu());
            d = d.plusDays(1);
        }

        for (Meal m : allMeals) {
            StatsAccu sa = accu.get(m.getDate());
            if (sa == null) continue;
            if (Boolean.TRUE.equals(m.getBreakfastTick())) sa.mealCount++;
            if (Boolean.TRUE.equals(m.getLunchTick()))     sa.mealCount++;
            if (Boolean.TRUE.equals(m.getDinnerTick()))    sa.mealCount++;
            if (Boolean.TRUE.equals(m.getSnacksTick()))    sa.mealCount++;
            if (Boolean.TRUE.equals(m.getSportTick()))     sa.sportCount++;
            if (Boolean.TRUE.equals(m.getWaterTick()))     sa.waterCount++;
        }

        List<Object[]> result = new ArrayList<>(accu.size());
        accu.forEach((dateKey, sa) ->
                result.add(new Object[]{ dateKey.toString(), sa.mealCount, sa.sportCount, sa.waterCount })
        );
        return result;
    }

    /**
     * 2a) Totaux pour une date.
     *    → 1 seul findByUserIdAndDate(), puis calculs en mémoire.
     */
    public Object[] getTotalsForDate(Long userId, LocalDate date) {
        List<Meal> meals = mealRepo.findByUserIdAndDate(userId, date);

        int mealCal   = calculateMealCaloriesFromList(meals);
        int sportCal  = activityRepo.findTotalCaloriesBurntByUserAndDate(userId, date);
        double waterL = calculateWaterMlFromList(meals) / 1000.0;

        return new Object[]{ date.toString(), mealCal, sportCal, waterL };
    }

    /**
     * 2b) Totaux pour une date + activité personnalisée.
     */
    public Object[] getTotalsForDate(Long userId,
                                     LocalDate date,
                                     String activity,
                                     int durationMinutes) {
        List<Meal> meals = mealRepo.findByUserIdAndDate(userId, date);

        int mealCal   = calculateMealCaloriesFromList(meals);
        int sportCal  = computeSportCalories(activity, durationMinutes);
        double waterL = calculateWaterMlFromList(meals) / 1000.0;

        return new Object[]{ date.toString(), mealCal, sportCal, waterL };
    }

    /**
     * 3) Pourcentage d’hydratation.
     */
    public double computeHydration(Long userId, LocalDate date) {
        List<Meal> meals = mealRepo.findByUserIdAndDate(userId, date);
        int totalMl = calculateWaterMlFromList(meals);
        double pct  = totalMl / (double) RECOMMENDED_WATER_ML * 100.0;
        return Math.min(100.0, pct);
    }

    /**
     * Calcule les calories sportives selon activité + durée.
     */
    public int computeSportCalories(String activityName, int durationMinutes) {
        if (activityName == null || activityName.isBlank()) return 0;
        String key = activityName.trim().toLowerCase();
        Integer kcalPerMin = CALORIES_PER_MINUTE.getOrDefault(key, 5);
        int duration = Math.max(0, durationMinutes);
        return kcalPerMin * duration;
    }

    // ────────── Méthodes auxiliaires ────────── //

    private int calculateMealCaloriesFromList(List<Meal> meals) {
        return meals.stream()
                .flatMap(m -> Stream.of(
                        Boolean.TRUE.equals(m.getBreakfastTick()) ? m.getBreakfast() : null,
                        Boolean.TRUE.equals(m.getLunchTick())     ? m.getLunch()     : null,
                        Boolean.TRUE.equals(m.getDinnerTick())    ? m.getDinner()    : null,
                        Boolean.TRUE.equals(m.getSnacksTick())    ? m.getSnacks()    : null
                ))
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .mapToInt(this::getCachedCalories)
                .sum();
    }

    private int calculateWaterMlFromList(List<Meal> meals) {
        return meals.stream()
                .filter(m -> Boolean.TRUE.equals(m.getWaterTick()))
                .map(Meal::getWater)
                .filter(Objects::nonNull)
                .mapToInt(this::parseWaterString)
                .sum();
    }

    private int getCachedCalories(String dishName) {
        return dishCaloriesCache.computeIfAbsent(dishName, this::lookupOrFetchCalories);
    }

    private int lookupOrFetchCalories(String dishName) {
        return dishRepo.findByNameIgnoreCase(dishName)
                .map(Dish::getCaloriesPerPortion)
                .orElseGet(() -> fetchCalories(dishName));
    }

    @SuppressWarnings("unchecked")
    private int fetchCalories(String query) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://world.openfoodfacts.org/cgi/search.pl"
                    + "?search_terms=" + encoded
                    + "&search_simple=1&action=process&json=1";
            Map<?,?> resp = rest.getForObject(url, Map.class);
            if (resp == null) return 0;
            var products = (List<Map<String,Object>>) resp.get("products");
            if (products.isEmpty()) return 0;
            var nutriments = (Map<String,Object>) products.get(0).get("nutriments");
            Object kcal100g = nutriments.get("energy-kcal_100g");
            return (kcal100g instanceof Number) ? ((Number) kcal100g).intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseWaterString(String raw) {
        String s = raw.trim().toLowerCase();
        try {
            String num = s.replaceAll("[^0-9.,]", "").replace(',', '.');
            double val = Double.parseDouble(num);
            if (s.contains("l") && !s.contains("ml")) {
                return (int) Math.round(val * 1000);
            } else {
                return (int) Math.round(val);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Classe interne pour accumuler les statistiques
    private static class StatsAccu {
        int mealCount  = 0;
        int sportCount = 0;
        int waterCount = 0;
    }
}
