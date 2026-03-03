package com.revconnect.repository;

import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User recipient;

    @BeforeEach
    void setUp() {
        recipient = new User();
        recipient.setUsername("recipient");
        recipient.setEmail("recipient@example.com");
        recipient.setPassword("password");
        recipient.setRole(UserRole.PERSONAL);
        entityManager.persist(recipient);
        entityManager.flush();
    }

    @Test
    void findByRecipientOrderByCreatedAtDesc_ReturnsNotifications() {
        Notification n1 = new Notification();
        n1.setRecipient(recipient);
        n1.setMessage("Notif 1");
        n1.setType("LIKE");
        entityManager.persist(n1);

        Notification n2 = new Notification();
        n2.setRecipient(recipient);
        n2.setMessage("Notif 2");
        n2.setType("COMMENT");
        entityManager.persist(n2);

        entityManager.flush();

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0).getMessage()).isEqualTo("Notif 2"); // Descending order
    }

    @Test
    void countByRecipientAndIsReadFalse_ReturnsCount() {
        Notification n1 = new Notification();
        n1.setRecipient(recipient);
        n1.setMessage("Unread");
        n1.setRead(false);
        entityManager.persist(n1);
        entityManager.flush();

        long count = notificationRepository.countByRecipientAndIsReadFalse(recipient);
        assertThat(count).isEqualTo(1);
    }
}
