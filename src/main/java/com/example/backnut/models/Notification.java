package com.example.backnut.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // ALERTE, RAPPEL, INFO

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Liée à un utilisateur

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String title, String message, NotificationType type, User user) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    // Getters & Setters
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    // Getters & Setters
}
