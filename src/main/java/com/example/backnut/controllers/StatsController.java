package com.example.backnut.controllers;

import com.example.backnut.services.StatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stat")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Stats journalières (ticks)
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<List<Object[]>> getUserStats(
            @PathVariable Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (startDate == null || endDate == null) {
            endDate   = LocalDate.now();
            startDate = endDate.minusDays(6);
        }
        return ResponseEntity.ok(
                statsService.getDailyStats(userId, startDate, endDate)
        );
    }

    /**
     * Totaux repas, sport, eau et pourcentage d'hydratation
     */
    @GetMapping("/user/{userId}/totals")
    public ResponseEntity<Map<String, Object>> getTotals(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) Integer duration
    ) {
        // Récupère les totaux de calories repas, sport et eau
        Object[] base = (activity != null && duration != null)
                ? statsService.getTotalsForDate(userId, date, activity, duration)
                : statsService.getTotalsForDate(userId, date);

        // Calcule le pourcentage d'hydratation
        double hydrationPercent = statsService.computeHydration(userId, date);

        Map<String, Object> response = new HashMap<>();
        response.put("date", base[0]);
        response.put("mealCalories", base[1]);
        response.put("sportCalories", base[2]);
        response.put("waterLiters", base[3]);
        response.put("hydrationPercent", hydrationPercent);

        return ResponseEntity.ok(response);
    }

    /**
     * Ancien endpoint pour hydration si besoin
     */
    @GetMapping("/user/{userId}/hydration")
    public ResponseEntity<Map<String, Double>> getHydration(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        double pct = statsService.computeHydration(userId, date);
        return ResponseEntity.ok(Map.of("hydrationPercent", pct));
    }
}