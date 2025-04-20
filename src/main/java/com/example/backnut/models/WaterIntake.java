package com.example.backnut.models;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "water_intakes")
public class WaterIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int quantity;     // en ml, par ex.

    @Column(nullable = false)
    private boolean tick;     // comme pour Meal.tick

    // getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isTick() { return tick; }
    public void setTick(boolean tick) { this.tick = tick; }
}
