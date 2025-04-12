package com.example.backnut.repository;

import com.example.backnut.models.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Récupérer la conversation entre un user et un coach s'il en existe une
    Optional<Conversation> findByUserIdAndCoachId(Long userId, Long coachId);
    List<Conversation> findByUser_Id(Long userId);

    // Récupérer toutes les conversations d'un coach
    List<Conversation> findByCoach_Id(Long coachId);
}
