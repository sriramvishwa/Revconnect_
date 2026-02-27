package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.FollowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{followerId}/{followingId}")
    public String followUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {

        return followService.followUser(followerId, followingId);
    }

    @DeleteMapping("/{followerId}/{followingId}")
    public String unfollowUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {

        return followService.unfollowUser(followerId, followingId);
    }

    @GetMapping("/followers/{userId}")
    public List<User> getFollowers(@PathVariable Long userId) {
        return followService.getFollowers(userId);
    }

    @GetMapping("/following/{userId}")
    public List<User> getFollowing(@PathVariable Long userId) {
        return followService.getFollowing(userId);
    }
}
