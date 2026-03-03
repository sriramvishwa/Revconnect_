package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface NotificationService {
    void notifyLike(User recipient, User sender, Post post);

    void notifyComment(User recipient, User sender, Post post);

    void notifyFollow(User recipient, User sender);

    void notifyConnectionRequest(User recipient, User sender);

    void notifyConnectionAccepted(User recipient, User sender);

    void notifyShare(User recipient, User sender, Post post);
}
