package com.example.backnut.repository;

import com.example.backnut.models.Notification;
import com.example.backnut.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserAndIsReadFalse(User user); // ✅ Récupère les notifications non lues
    int countByUserAndIsReadFalse(User user); // ✅ Compte les notifications non lues
    long count();
}
