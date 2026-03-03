package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.entity.User;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectionServiceImplTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");
    }

    @Test
    void sendConnectionRequest_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());
        when(connectionRepository.findByReceiverAndSender(sender, receiver)).thenReturn(Optional.empty());

        String result = connectionService.sendConnectionRequest(1L, 2L);

        assertThat(result).isEqualTo("Connection request sent successfully");
        verify(connectionRepository, times(1)).save(any(Connection.class));
        verify(notificationService, times(1)).notifyConnectionRequest(receiver, sender);
    }

    @Test
    void sendConnectionRequest_SameUser_ReturnsError() {
        String result = connectionService.sendConnectionRequest(1L, 1L);
        assertThat(result).isEqualTo("You cannot connect with yourself");
    }

    @Test
    void acceptConnectionRequest_Success() {
        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(connection));

        String result = connectionService.acceptConnectionRequest(2L, 1L);

        assertThat(result).isEqualTo("Connection request accepted");
        assertThat(connection.getStatus()).isEqualTo(ConnectionStatus.ACCEPTED);
        verify(notificationService, times(1)).notifyConnectionAccepted(sender, receiver);
    }
}
