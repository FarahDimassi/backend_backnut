package com.example.backnut.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meals")
public class Meal {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Identifiant du coach qui ajoute les repas
        private Long coachId;

        // Identifiant de l'utilisateur pour qui les repas sont ajoutés
        private Long userId;

        // Champs correspondant aux différents types de repas
        private String breakfast;
        private String lunch;
        private String dinner;
        private String snacks;
        private String sport;
        private String water;

        // Date d'enregistrement pour la journée concernée
        private LocalDate date;

    // ------------------ Tick fields ------------------
    private Boolean breakfastTick = false;
    private Boolean lunchTick     = false;
    private Boolean dinnerTick    = false;
    private Boolean snacksTick    = false;
    private Boolean sportTick     = false;
    private Boolean waterTick     = false;
    // -------------------------------------------------
        // --- Constructeur par défaut ---
        public Meal() {
        }

        // --- Constructeur avec paramètres ---
        public Meal(Long coachId, Long userId, String breakfast, String lunch, String dinner,
                         String snacks, String sport, String water, LocalDate date) {
            this.coachId = coachId;
            this.userId = userId;
            this.breakfast = breakfast;
            this.lunch = lunch;
            this.dinner = dinner;
            this.snacks = snacks;
            this.sport = sport;
            this.water = water;
            this.date = date;
        }

        // --- Getters et Setters ---

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCoachId() {
            return coachId;
        }

        public void setCoachId(Long coachId) {
            this.coachId = coachId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getBreakfast() {
            return breakfast;
        }

        public void setBreakfast(String breakfast) {
            this.breakfast = breakfast;
        }

        public String getLunch() {
            return lunch;
        }

        public void setLunch(String lunch) {
            this.lunch = lunch;
        }

        public String getDinner() {
            return dinner;
        }

        public void setDinner(String dinner) {
            this.dinner = dinner;
        }

        public String getSnacks() {
            return snacks;
        }

        public void setSnacks(String snacks) {
            this.snacks = snacks;
        }

        public String getSport() {
            return sport;
        }

        public void setSport(String sport) {
            this.sport = sport;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    public Boolean getBreakfastTick() {
        return breakfastTick;
    }

    public void setBreakfastTick(Boolean breakfastTick) {
        this.breakfastTick = breakfastTick;
    }

    public Boolean getLunchTick() {
        return lunchTick;
    }

    public void setLunchTick(Boolean lunchTick) {
        this.lunchTick = lunchTick;
    }

    public Boolean getDinnerTick() {
        return dinnerTick;
    }

    public void setDinnerTick(Boolean dinnerTick) {
        this.dinnerTick = dinnerTick;
    }

    public Boolean getSnacksTick() {
        return snacksTick;
    }

    public void setSnacksTick(Boolean snacksTick) {
        this.snacksTick = snacksTick;
    }

    public Boolean getSportTick() {
        return sportTick;
    }

    public void setSportTick(Boolean sportTick) {
        this.sportTick = sportTick;
    }

    public Boolean getWaterTick() {
        return waterTick;
    }

    public void setWaterTick(Boolean waterTick) {
        this.waterTick = waterTick;
    }
    }

