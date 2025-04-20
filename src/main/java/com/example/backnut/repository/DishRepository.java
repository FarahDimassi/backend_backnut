package com.example.backnut.repository;

import com.example.backnut.models.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> findByNameIgnoreCase(String name);
}
