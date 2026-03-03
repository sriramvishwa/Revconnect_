package com.revconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    // ✅ FIXED FIELD NAME
    private boolean read;

    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {   // ✅ matches mapper
        return read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}