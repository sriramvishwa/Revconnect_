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

    @PostMapping("/request")
    public String sendConnectionRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        return connectionService.sendConnectionRequest(senderId, receiverId);
    }

    @PutMapping("/accept")
    public String acceptPut(@RequestParam Long receiverId, @RequestParam Long senderId) {
        return connectionService.acceptConnectionRequest(receiverId, senderId);
    }

    @PostMapping("/accept")
    public String acceptPost(@RequestParam Long receiverId, @RequestParam Long senderId) {
        return connectionService.acceptConnectionRequest(receiverId, senderId);
    }

    @PutMapping("/reject")
    public String rejectPut(@RequestParam Long receiverId, @RequestParam Long senderId) {
        return connectionService.rejectConnectionRequest(receiverId, senderId);
    }

    @PostMapping("/reject")
    public String rejectPost(@RequestParam Long receiverId, @RequestParam Long senderId) {
        return connectionService.rejectConnectionRequest(receiverId, senderId);
    }

    @GetMapping("/{userId}")
    public List<User> getConnections(@PathVariable Long userId) {
        return connectionService.getConnections(userId);
    }

    @GetMapping("/pending/{userId}")
    public List<Connection> getPendingRequests(@PathVariable Long userId) {
        return connectionService.getPendingRequests(userId);
    }

    @DeleteMapping("/remove")
    public String removeConnection(@RequestParam Long userId1, @RequestParam Long userId2) {
        return connectionService.removeConnection(userId1, userId2);
    }

    @PostMapping("/remove")
    public String removeConnectionPost(@RequestParam Long userId1, @RequestParam Long userId2) {
        return connectionService.removeConnection(userId1, userId2);
    }
}
