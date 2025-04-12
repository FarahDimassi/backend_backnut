package com.example.backnut.repository;

import com.example.backnut.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Récupérer tous les messages d'une conversation
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
}