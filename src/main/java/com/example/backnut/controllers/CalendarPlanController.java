package com.example.backnut.controllers;

import com.example.backnut.models.CalendarPlan;
import com.example.backnut.services.CalendarPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendar")
public class CalendarPlanController {

    @Autowired
    private CalendarPlanService calendarPlanService;

    // Créer un nouveau plan
    @PostMapping("/plan")
    public ResponseEntity<CalendarPlan> createPlan(@RequestBody CalendarPlan plan) {
        try {
            CalendarPlan savedPlan = calendarPlanService.createOrUpdatePlan(plan);
            return ResponseEntity.ok(savedPlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Mettre à jour un plan existant par ID


    // Récupérer les plans (GET) pour un utilisateur et un coach ou filtré par date
    @GetMapping("/plan")
    public ResponseEntity<List<CalendarPlan>> getPlans(
            @RequestParam Long userId,
            @RequestParam Long coachId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date != null) {
                List<CalendarPlan> plans = calendarPlanService.getPlansForUserAndCoachAndDate(userId, coachId, date);
                return ResponseEntity.ok(plans);
            } else {
                List<CalendarPlan> plans = calendarPlanService.getPlansForUserAndCoach(userId, coachId);
                return ResponseEntity.ok(plans);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint pour créer ou mettre à jour un plan via une seule opération
    // Remarque : ici, "username" correspond au champ qui était auparavant "title" et "remarque" remplace "description"
    @PostMapping("/plan/addOrUpdate")
    public ResponseEntity<CalendarPlan> addOrUpdatePlan(
            @RequestParam Long userId,
            @RequestParam Long coachId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String username,
            @RequestParam String remarque) {
        try {
            CalendarPlan plan = calendarPlanService.addOrUpdateCalendarPlan(userId, coachId, date, username, remarque);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/plan/remarks")
    public ResponseEntity<List<Map<String, String>>> getCalendarPlanRemarks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long coachId) {
        try {
            // Récupérer tous les CalendarPlan pour le coach et la date spécifiée
            List<CalendarPlan> plans = calendarPlanService.getPlansForCoachAndDate(coachId, date);

            // Mapper la liste pour ne renvoyer que le champ "remarque" et "userId" dans chaque objet.
            List<Map<String, String>> remarks = plans.stream().map(plan -> {
                Map<String, String> map = new HashMap<>();
                // Assurez-vous que CalendarPlan possède un getter getUserId() et que le type est convertible en String.
                map.put("userId", plan.getUserId().toString());
                map.put("remarque", plan.getRemarque());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(remarks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/remark/{id}")
    public ResponseEntity<Void> deleteRemark(@PathVariable Long id) {
        try {
            calendarPlanService.deleteRemark(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Loguer l'erreur si besoin
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/user")
    public ResponseEntity<List<CalendarPlan>> getPlansForUserAndDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<CalendarPlan> plans = calendarPlanService.getPlansForUserAndDate(userId, date);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
