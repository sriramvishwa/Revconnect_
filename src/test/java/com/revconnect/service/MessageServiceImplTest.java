package com.revconnect.service;

import com.revconnect.entity.Message;
import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.repository.MessageRepository;
import com.revconnect.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = new User();
        userA.setId(1L);
        userA.setUsername("userA");

        userB = new User();
        userB.setId(2L);
        userB.setUsername("userB");
    }

    @Test
    void getConversations_ReturnsPartners() {
        Message m1 = new Message();
        m1.setSender(userA);
        m1.setReceiver(userB);

        List<Message> messages = new ArrayList<>();
        messages.add(m1);

        when(messageRepository.findBySenderOrReceiver(userA, userA)).thenReturn(messages);

        List<User> partners = messageService.getConversations(userA);

        assertThat(partners).hasSize(1);
        assertThat(partners.get(0).getUsername()).isEqualTo("userB");
    }

    @Test
    void sendMessage_Success() {
        Message msg = new Message();
        msg.setId(10L);
        msg.setSender(userA);
        msg.setReceiver(userB);
        msg.setContent("Hello");

        when(messageRepository.save(any(Message.class))).thenReturn(msg);

        Message result = messageService.sendMessage(userA, userB, "Hello");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello");
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void sendMessage_EmptyContent_ReturnsNull() {
        Message result = messageService.sendMessage(userA, userB, "");
        assertThat(result).isNull();
    }
}
