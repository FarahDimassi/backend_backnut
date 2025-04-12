package com.example.backnut.services;

import com.example.backnut.models.Notification;
import com.example.backnut.models.NotificationType;
import com.example.backnut.models.User;
import com.example.backnut.repository.NotificationRepository;
import com.example.backnut.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.error("❌ Utilisateur {} non trouvé", userId);
            throw new RuntimeException("Utilisateur introuvable !");
        }
        return notificationRepository.findByUser(user);
    }

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        logger.info("📢 Création d'une notification pour userId: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.error("❌ Utilisateur introuvable pour userId: {}", userId);
            throw new RuntimeException("Utilisateur introuvable !");
        }

        User user = userOpt.get();
        Notification notification = new Notification(title, message, type, user);
        logger.info("✅ Notification créée : {}", notification);

        return notificationRepository.save(notification);
    }

    public Notification updateNotification(Long id, String title, String message) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        notification.setTitle(title);
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            logger.error("❌ Tentative de suppression d'une notification inexistante: {}", id);
            throw new RuntimeException("Notification introuvable");
        }
        notificationRepository.deleteById(id);
    }

    // ✅ Compter les notifications non lues
    public int countUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    // ✅ Marquer une notification comme lue
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

}
