package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // GET USER NOTIFICATIONS
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    // MARK AS READ
    @PutMapping("/read/{notificationId}")
    public String markAsRead(@PathVariable Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow();

        notification.setRead(true);

        notificationRepository.save(notification);

        return "Notification marked as read";
    }

}
