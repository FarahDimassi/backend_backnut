package com.example.backnut.repository;
import com.example.backnut.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Vous pouvez ajouter des méthodes spécifiques si nécessaire, par exemple :
    List<Review> findByCoachId(Long coachId);
}
