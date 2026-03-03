package com.revconnect.repository;

import com.revconnect.entity.Message;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = new User();
        userA.setUsername("userA");
        userA.setEmail("a@example.com");
        userA.setPassword("password");
        userA.setRole(UserRole.PERSONAL);
        entityManager.persist(userA);

        userB = new User();
        userB.setUsername("userB");
        userB.setEmail("b@example.com");
        userB.setPassword("password");
        userB.setRole(UserRole.PERSONAL);
        entityManager.persist(userB);

        entityManager.flush();
    }

    @Test
    void findThread_ReturnsMessagesInOrder() {
        Message m1 = new Message();
        m1.setContent("Hello");
        m1.setSender(userA);
        m1.setReceiver(userB);
        m1.setSentAt(LocalDateTime.now().minusMinutes(5));
        entityManager.persist(m1);

        Message m2 = new Message();
        m2.setContent("Hi");
        m2.setSender(userB);
        m2.setReceiver(userA);
        m2.setSentAt(LocalDateTime.now());
        entityManager.persist(m2);

        entityManager.flush();

        List<Message> thread = messageRepository.findThread(userA, userB);
        assertThat(thread).hasSize(2);
        assertThat(thread.get(0).getContent()).isEqualTo("Hello");
        assertThat(thread.get(1).getContent()).isEqualTo("Hi");
    }

    @Test
    void countUnreadByReceiver_ReturnsCount() {
        Message m1 = new Message();
        m1.setContent("Unread");
        m1.setSender(userA);
        m1.setReceiver(userB);
        m1.setRead(false);
        m1.setSentAt(LocalDateTime.now());
        entityManager.persist(m1);
        entityManager.flush();

        long count = messageRepository.countUnreadByReceiver(userB);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void markThreadAsRead_UpdatesStatus() {
        Message m1 = new Message();
        m1.setContent("Unread");
        m1.setSender(userA);
        m1.setReceiver(userB);
        m1.setRead(false);
        m1.setSentAt(LocalDateTime.now());
        entityManager.persist(m1);
        entityManager.flush();

        messageRepository.markThreadAsRead(userA, userB);
        entityManager.flush();
        entityManager.clear();

        long count = messageRepository.countUnreadByReceiver(userB);
        assertThat(count).isEqualTo(0);
    }
}
