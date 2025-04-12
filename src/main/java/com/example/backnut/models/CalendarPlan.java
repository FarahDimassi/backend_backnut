package com.example.backnut.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "calendar_plan", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "coachId", "date"})
})
public class CalendarPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long coachId;
    private LocalDate date;

    // Dans cette version, username remplace title, et remarque remplace description

    private String remarque;

    public CalendarPlan() {}

    public CalendarPlan(Long userId, Long coachId, LocalDate date, String remarque) {
        this.userId = userId;
        this.coachId = coachId;
        this.date = date;

        this.remarque = remarque;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Remplace title par username


    // Remplace description par remarque
    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }
}
