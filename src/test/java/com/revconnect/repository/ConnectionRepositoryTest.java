package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setUsername("sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setRole(UserRole.PERSONAL);
        entityManager.persist(sender);

        receiver = new User();
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        receiver.setRole(UserRole.PERSONAL);
        entityManager.persist(receiver);

        entityManager.flush();
    }

    @Test
    void findBySenderAndReceiver_ReturnsConnection() {
        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);
        entityManager.persist(connection);
        entityManager.flush();

        Optional<Connection> found = connectionRepository.findBySenderAndReceiver(sender, receiver);
        assertThat(found).isPresent();
        assertThat(found.get().getSender().getUsername()).isEqualTo("sender");
    }

    @Test
    void findByReceiverAndStatus_ReturnsList() {
        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);
        entityManager.persist(connection);
        entityManager.flush();

        List<Connection> pending = connectionRepository.findByReceiverAndStatus(receiver, ConnectionStatus.PENDING);
        assertThat(pending).hasSize(1);
    }
}
