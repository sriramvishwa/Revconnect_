package com.revconnect.service;

import com.revconnect.entity.Message;
import com.revconnect.entity.User;

import java.util.List;

public interface MessageService {
    List<User> getConversations(User user);

    List<Message> getThread(User me, User other);

    Message sendMessage(User from, User to, String content);

    Message getLastMessage(User me, User other);

    long countUnreadMessages(User receiver);

    void markThreadAsRead(User sender, User receiver);
}
