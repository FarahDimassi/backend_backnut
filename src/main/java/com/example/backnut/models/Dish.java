package com.example.backnut.models;

import jakarta.persistence.*;

@Entity
@Table(name = "dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;           // ex. "toast", "salade", "pomme"

    @Column(name = "calories_per_portion", nullable = false)
    private int caloriesPerPortion;

    // Constructeurs, getters, setters...

    public Dish() {}
    public Dish(String name, int caloriesPerPortion) {
        this.name = name;
        this.caloriesPerPortion = caloriesPerPortion;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public int getCaloriesPerPortion() { return caloriesPerPortion; }
    public void setCaloriesPerPortion(int c) { this.caloriesPerPortion = c; }
}
