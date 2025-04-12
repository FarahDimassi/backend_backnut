package com.example.backnut.repository;

import com.example.backnut.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = :date")
    long countUsersByCreatedAt(@Param("date") LocalDate date);

}