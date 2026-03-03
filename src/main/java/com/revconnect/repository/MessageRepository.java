package com.revconnect.repository;

import com.revconnect.entity.Message;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

        // All messages between two users, ordered by time
        @Query("SELECT m FROM Message m WHERE " +
                        "(m.sender = :a AND m.receiver = :b) OR (m.sender = :b AND m.receiver = :a) " +
                        "ORDER BY m.sentAt ASC")
        List<Message> findThread(@Param("a") User a, @Param("b") User b);

        // All messages where the user is sender OR receiver
        List<Message> findBySenderOrReceiver(User sender, User receiver);

        // Latest message in a thread between two users
        @Query("SELECT m FROM Message m WHERE " +
                        "(m.sender = :a AND m.receiver = :b) OR (m.sender = :b AND m.receiver = :a) " +
                        "ORDER BY m.sentAt DESC")
        List<Message> findThreadDesc(@Param("a") User a, @Param("b") User b);

        // Count unread messages where the user is the receiver
        @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.read = false")
        long countUnreadByReceiver(@Param("receiver") User receiver);

        // Mark all messages from a sender to a receiver as read
        @Modifying
        @Query("UPDATE Message m SET m.read = true WHERE m.sender = :sender AND m.receiver = :receiver AND m.read = false")
        void markThreadAsRead(@Param("sender") User sender, @Param("receiver") User receiver);
}
