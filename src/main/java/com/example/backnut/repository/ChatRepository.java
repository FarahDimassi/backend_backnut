package com.example.backnut.repository;

import com.example.backnut.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE (c.senderId = :userId AND c.receiverId = :receiverId) OR (c.senderId = :receiverId AND c.receiverId = :userId) ORDER BY c.date ASC")
    List<Chat> findChatHistory(@Param("userId") Long userId, @Param("receiverId") Long receiverId);
    @Query("SELECT c FROM Chat c WHERE c.senderId = :id OR c.receiverId = :id")
    List<Chat> findChatsByUserId(@Param("id") Long id1, @Param("id") Long id2);

}