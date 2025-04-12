package com.example.backnut.controllers;

import com.example.backnut.models.Notification;
import com.example.backnut.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/notifications")
public class NotifUserController {

    private final NotificationService notificationService;

    public NotifUserController(NotificationService notificationService) { // ✅ Correction du nom du constructeur
        this.notificationService = notificationService;
    }

    // ✅ Récupérer toutes les notifications d'un utilisateur
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
//        List<Notification> notifications = notificationService.getNotificationsByUser(userId);
//        return ResponseEntity.ok(notifications);
//    }

    // ✅ Compter les notifications non lues
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Map<String, Integer>> getUnreadNotificationsCount(@PathVariable Long userId) {
        int unreadCount = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    // ✅ Marquer une notification comme lue
    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<Map<String, String>> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(Map.of("message", "Notification marquée comme lue"));
    }
}
