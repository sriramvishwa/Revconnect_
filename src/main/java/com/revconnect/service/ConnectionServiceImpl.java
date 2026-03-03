package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.entity.User;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

        private final ConnectionRepository connectionRepository;
        private final UserRepository userRepository;
        private final NotificationService notificationService;

        public ConnectionServiceImpl(ConnectionRepository connectionRepository,
                        UserRepository userRepository,
                        NotificationService notificationService) {
                this.connectionRepository = connectionRepository;
                this.userRepository = userRepository;
                this.notificationService = notificationService;
        }

        // Send connection request
        @Override
        public String sendConnectionRequest(Long senderId, Long receiverId) {

                if (senderId.equals(receiverId)) {
                        return "You cannot connect with yourself";
                }

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));

                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));

                // Check if connection already exists
                Optional<Connection> existingConnection = connectionRepository.findBySenderAndReceiver(sender,
                                receiver);

                Optional<Connection> reverseConnection = connectionRepository.findByReceiverAndSender(sender, receiver);

                if (existingConnection.isPresent()) {
                        Connection conn = existingConnection.get();
                        if (conn.getStatus() == ConnectionStatus.REJECTED) {
                                connectionRepository.delete(conn);
                        } else {
                                return "Connection already exists or pending";
                        }
                }

                if (reverseConnection.isPresent()) {
                        Connection conn = reverseConnection.get();
                        if (conn.getStatus() == ConnectionStatus.REJECTED) {
                                connectionRepository.delete(conn);
                        } else {
                                return "Connection already exists or pending";
                        }
                }

                Connection connection = new Connection();
                connection.setSender(sender);
                connection.setReceiver(receiver);
                connection.setStatus(ConnectionStatus.PENDING);

                connectionRepository.save(connection);

                // Notify receiver
                notificationService.notifyConnectionRequest(receiver, sender);

                return "Connection request sent successfully";
        }

        // Accept connection request
        @Override
        public String acceptConnectionRequest(Long receiverId, Long senderId) {

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));

                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));

                Connection connection = connectionRepository
                                .findBySenderAndReceiver(sender, receiver)
                                .orElseThrow(() -> new RuntimeException("Connection request not found"));

                if (connection.getStatus() != ConnectionStatus.PENDING) {
                        return "Connection already processed";
                }

                connection.setStatus(ConnectionStatus.ACCEPTED);

                connectionRepository.save(connection);

                // Notify original sender that their request was accepted
                notificationService.notifyConnectionAccepted(sender, receiver);

                return "Connection request accepted";
        }

        // Reject connection request
        @Override
        public String rejectConnectionRequest(Long receiverId, Long senderId) {

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));

                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));

                Connection connection = connectionRepository
                                .findBySenderAndReceiver(sender, receiver)
                                .orElseThrow(() -> new RuntimeException("Connection request not found"));

                connection.setStatus(ConnectionStatus.REJECTED);

                connectionRepository.save(connection);

                return "Connection request rejected";
        }

        // Get all accepted connections
        @Override
        public List<User> getConnections(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<Connection> sentConnections = connectionRepository.findBySenderAndStatus(user,
                                ConnectionStatus.ACCEPTED);

                List<Connection> receivedConnections = connectionRepository.findByReceiverAndStatus(user,
                                ConnectionStatus.ACCEPTED);

                List<User> connections = new ArrayList<>();

                for (Connection connection : sentConnections) {
                        connections.add(connection.getReceiver());
                }

                for (Connection connection : receivedConnections) {
                        connections.add(connection.getSender());
                }

                return connections;
        }

        // Get pending requests received
        @Override
        public List<Connection> getPendingRequests(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return connectionRepository.findByReceiverAndStatus(user, ConnectionStatus.PENDING);
        }

        // Remove connection
        @Override
        public String removeConnection(Long userId1, Long userId2) {

                User user1 = userRepository.findById(userId1)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                User user2 = userRepository.findById(userId2)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Optional<Connection> connection = connectionRepository.findBySenderAndReceiver(user1, user2);

                if (connection.isEmpty()) {
                        connection = connectionRepository.findByReceiverAndSender(user1, user2);
                }

                if (connection.isPresent()) {

                        connectionRepository.delete(connection.get());

                        return "Connection removed successfully";
                }

                return "Connection not found";
        }

}
