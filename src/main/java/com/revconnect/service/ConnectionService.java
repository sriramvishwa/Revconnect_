package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;

import java.util.List;

public interface ConnectionService {

    String sendConnectionRequest(Long senderId, Long receiverId);

    String acceptConnectionRequest(Long receiverId, Long senderId);

    String rejectConnectionRequest(Long receiverId, Long senderId);

    List<User> getConnections(Long userId);

    List<Connection> getPendingRequests(Long userId);

    String removeConnection(Long userId1, Long userId2);
}
