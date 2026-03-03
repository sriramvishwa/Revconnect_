package com.revconnect.service;

import com.revconnect.entity.Message;
import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.repository.MessageRepository;
import com.revconnect.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;

    public MessageServiceImpl(MessageRepository messageRepository, NotificationRepository notificationRepository) {
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<User> getConversations(User user) {
        List<Message> messages = messageRepository.findBySenderOrReceiver(user, user);
        Set<User> partners = new LinkedHashSet<>();
        for (Message m : messages) {
            if (!m.getSender().getId().equals(user.getId()))
                partners.add(m.getSender());
            if (!m.getReceiver().getId().equals(user.getId()))
                partners.add(m.getReceiver());
        }
        return new ArrayList<>(partners);
    }

    @Override
    public List<Message> getThread(User me, User other) {
        return messageRepository.findThread(me, other);
    }

    @Override
    public Message getLastMessage(User me, User other) {
        List<Message> desc = messageRepository.findThreadDesc(me, other);
        return desc.isEmpty() ? null : desc.get(0);
    }

    @Override
    public Message sendMessage(User from, User to, String content) {
        if (content == null || content.isBlank())
            return null;

        Message msg = new Message();
        msg.setSender(from);
        msg.setReceiver(to);
        msg.setContent(content.trim());
        msg.setSentAt(LocalDateTime.now());
        msg.setRead(false);
        Message saved = messageRepository.save(msg);

        // Notify recipient
        Notification n = new Notification();
        n.setRecipient(to);
        n.setSender(from);
        n.setType("MESSAGE");
        n.setMessage(from.getUsername() + " sent you a message");
        n.setRelatedEntityId(saved.getId());
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(n);

        return saved;
    }

    @Override
    public long countUnreadMessages(User receiver) {
        return messageRepository.countUnreadByReceiver(receiver);
    }

    @Override
    @Transactional
    public void markThreadAsRead(User sender, User receiver) {
        messageRepository.markThreadAsRead(sender, receiver);
    }
}
