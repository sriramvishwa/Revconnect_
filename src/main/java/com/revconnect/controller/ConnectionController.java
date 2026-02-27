package com.revconnect.controller;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    // Send connection request
    @PostMapping("/request")
    public String sendConnectionRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {

        return connectionService.sendConnectionRequest(senderId, receiverId);
    }

    // Accept connection request
    @PutMapping("/accept")
    public String acceptConnectionRequest(
            @RequestParam Long receiverId,
            @RequestParam Long senderId) {

        return connectionService.acceptConnectionRequest(receiverId, senderId);
    }

    // Reject connection request
    @PutMapping("/reject")
    public String rejectConnectionRequest(
            @RequestParam Long receiverId,
            @RequestParam Long senderId) {

        return connectionService.rejectConnectionRequest(receiverId, senderId);
    }

    // Get all connections
    @GetMapping("/{userId}")
    public List<User> getConnections(@PathVariable Long userId) {

        return connectionService.getConnections(userId);
    }

    // Get pending requests
    @GetMapping("/pending/{userId}")
    public List<Connection> getPendingRequests(@PathVariable Long userId) {

        return connectionService.getPendingRequests(userId);
    }

    // Remove connection
    @DeleteMapping("/remove")
    public String removeConnection(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {

        return connectionService.removeConnection(userId1, userId2);
    }
}
