package com.revconnect.service;

import com.revconnect.entity.Notification;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void notifyLike_ShouldSaveNotification() {
        User recipient = new User();
        recipient.setId(1L);
        User sender = new User();
        sender.setId(2L);
        sender.setUsername("sender");
        Post post = new Post();
        post.setId(3L);

        notificationService.notifyLike(recipient, sender, post);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void notifyFollow_ShouldSaveNotification() {
        User recipient = new User();
        recipient.setId(1L);
        User sender = new User();
        sender.setId(2L);
        sender.setUsername("sender");

        notificationService.notifyFollow(recipient, sender);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void notifyLike_SameUser_ShouldNotSave() {
        User user = new User();
        user.setId(1L);
        Post post = new Post();
        post.setId(2L);

        notificationService.notifyLike(user, user, post);

        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
