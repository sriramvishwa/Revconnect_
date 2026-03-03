package com.revconnect.service;

import com.revconnect.entity.Notification;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private void save(User recipient, User sender, String type, String message, Long relatedEntityId) {
        // Don't notify yourself
        if (recipient.getId().equals(sender.getId()))
            return;

        // Check notification preferences (comma-separated disabled types stored on
        // user)
        String prefs = recipient.getNotificationPrefs();
        if (prefs != null && !prefs.isBlank()) {
            for (String disabled : prefs.split(",")) {
                if (disabled.trim().equalsIgnoreCase(type))
                    return;
            }
        }

        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setSender(sender);
        n.setType(type);
        n.setMessage(message);
        n.setRelatedEntityId(relatedEntityId);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(n);
    }

    @Override
    public void notifyLike(User recipient, User sender, Post post) {
        save(recipient, sender, "LIKE",
                sender.getUsername() + " liked your post", post.getId());
    }

    @Override
    public void notifyComment(User recipient, User sender, Post post) {
        save(recipient, sender, "COMMENT",
                sender.getUsername() + " commented on your post", post.getId());
    }

    @Override
    public void notifyFollow(User recipient, User sender) {
        save(recipient, sender, "FOLLOW",
                sender.getUsername() + " started following you", null);
    }

    @Override
    public void notifyConnectionRequest(User recipient, User sender) {
        save(recipient, sender, "CONNECTION_REQUEST",
                sender.getUsername() + " sent you a connection request", null);
    }

    @Override
    public void notifyConnectionAccepted(User recipient, User sender) {
        save(recipient, sender, "CONNECTION_ACCEPTED",
                sender.getUsername() + " accepted your connection request", null);
    }

    @Override
    public void notifyShare(User recipient, User sender, Post post) {
        save(recipient, sender, "SHARE",
                sender.getUsername() + " shared your post", post.getId());
    }
}
