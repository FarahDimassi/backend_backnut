package com.example.backnut.repository;

import com.example.backnut.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT COALESCE(SUM(a.caloriesBurnt),0) " +
            "FROM Activity a " +
            "WHERE a.user.id = :userId " +
            "  AND a.date = :date")
    int findTotalCaloriesBurntByUserAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date);
}
