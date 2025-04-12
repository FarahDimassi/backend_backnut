package com.example.backnut.services;

import com.example.backnut.repository.UserRepository;
import com.example.backnut.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AdminDashboardService(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count()); // 🔹 Nombre total d'utilisateurs
        stats.put("totalNotifications", notificationRepository.count()); // 🔹 Nombre total de notifications
        stats.put("usersPerDay", getUsersPerDay());
        return stats;
    }

    public Map<String, Long> getUsersPerDay() {
        Map<String, Long> usersPerDay = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = userRepository.countUsersByCreatedAt(date);
            usersPerDay.put(days[6 - i], count);
        }

        return usersPerDay;
    }
}
