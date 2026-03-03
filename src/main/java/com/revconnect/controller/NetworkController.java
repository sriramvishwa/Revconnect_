package com.revconnect.controller;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.MessageService;
import com.revconnect.service.NotificationService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/network")
public class NetworkController {

    @Autowired
    private UserService userService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private FollowService followService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String network(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";

        User currentUser = userService.findByUsername(principal.getName());

        List<User> connections = connectionService.getConnections(currentUser.getId());
        List<Connection> pending = connectionService.getPendingRequests(currentUser.getId());
        List<User> followers = followService.getFollowers(currentUser.getId());
        List<User> following = followService.getFollowing(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("connections", connections);
        model.addAttribute("pendingRequests", pending);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        model.addAttribute("unreadMessageCount", messageService.countUnreadMessages(currentUser));

        return "network";
    }

    @PostMapping("/follow/{userId}")
    public String toggleFollow(@PathVariable Long userId, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        User currentUser = userService.findByUsername(principal.getName());
        User targetUser = userService.findById(userId);

        if (followService.isFollowing(currentUser.getId(), userId)) {
            followService.unfollowUser(currentUser.getId(), userId);
        } else {
            followService.followUser(currentUser.getId(), userId);
            notificationService.notifyFollow(targetUser, currentUser);
        }

        return "redirect:/profile/" + targetUser.getUsername();
    }
}
