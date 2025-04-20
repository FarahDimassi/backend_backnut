package com.example.backnut.repository;


import com.example.backnut.models.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findTopByUserIdOrderByDateDesc(Long userId);

    /**
     * Renvoie la liste des userId distincts présents dans la table.
     */
    @Query("select distinct up.userId from UserProgress up")
    List<Long> findDistinctUserIds();
}
