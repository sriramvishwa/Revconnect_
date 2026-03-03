package com.revconnect.controller;

import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.MessageService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @GetMapping
    public String notificationsPage(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";
        User user = userService.findByUsername(principal.getName());
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(user));
        return "notifications";
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public List<Notification> getNotifications(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    @PostMapping("/read/{notificationId}")
    @ResponseBody
    public String markAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
        return "ok";
    }

    @PostMapping("/read-all")
    @ResponseBody
    public String markAllRead(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        return "ok";
    }

    @PutMapping("/read/{notificationId}")
    @ResponseBody
    public String markAsReadPut(@PathVariable Long notificationId) {
        return markAsRead(notificationId);
    }
}
