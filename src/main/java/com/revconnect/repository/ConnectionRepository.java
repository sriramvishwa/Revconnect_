package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // Find connection between two users
    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    Optional<Connection> findByReceiverAndSender(User receiver, User sender);

    // Pending requests received
    List<Connection> findByReceiverAndStatus(User receiver, ConnectionStatus status);

    // Pending requests sent OR accepted sent
    List<Connection> findBySenderAndStatus(User sender, ConnectionStatus status);

}
