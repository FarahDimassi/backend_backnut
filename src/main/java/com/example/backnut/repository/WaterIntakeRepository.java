package com.example.backnut.repository;

import com.example.backnut.models.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {

    @Query("SELECT COALESCE(SUM(w.quantity),0) " +
            "FROM WaterIntake w " +
            "WHERE w.user.id = :userId " +
            "  AND w.date = :date " +
            "  AND w.tick = true")
    int findTotalWaterIntakeByUserAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date);
}
