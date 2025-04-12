package com.example.backnut.repository;

import com.example.backnut.models.ScannedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScannedProductRepository extends JpaRepository<ScannedProduct, Long> {
    List<ScannedProduct> findByUserId(Long userId);
    @Query("SELECT s FROM ScannedProduct s WHERE s.userId = :userId AND DATE(s.scanDate) = :date")
    List<ScannedProduct> findByUserIdAndExactDate(@Param("userId") Long userId, @Param("date") LocalDate date);

}

