package com.example.backnut.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // On suppose qu'une conversation est entre deux utilisateurs (client et coach)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Le client

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private User coach; // Le coach

    // Date de création
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setCoach(User coach) {
        this.coach = coach;
    }

    public User getCoach() {
        return coach;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Vous pouvez ajouter d'autres champs si nécessaire

    // Getters, setters, etc.
}

