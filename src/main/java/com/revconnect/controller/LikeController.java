package com.revconnect.controller;

import com.revconnect.entity.Post;
import com.revconnect.repository.PostRepository;
import com.revconnect.service.LikeService;
import com.revconnect.service.NotificationService;
import com.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostRepository postRepository;

    // Toggle like - used from feed (POST to /api/likes/{postId}/{userId})
    @PostMapping("/api/likes/{postId}/{userId}")
    public String toggleLike(@PathVariable Long postId, @PathVariable Long userId, Principal principal) {
        Post post = postRepository.findById(postId).orElse(null);
        // Try to unlike first, if not liked then like
        try {
            return likeService.unlikePost(userId, postId);
        } catch (RuntimeException e) {
            String result = likeService.likePost(userId, postId);
            // Fire notification
            if (post != null && principal != null) {
                var liker = userService.findByUsername(principal.getName());
                if (liker != null)
                    notificationService.notifyLike(post.getAuthor(), liker, post);
            }
            return result;
        }
    }

    @PostMapping("/likes/{userId}/{postId}")
    public String likePost(@PathVariable Long userId, @PathVariable Long postId) {
        return likeService.likePost(userId, postId);
    }

    @DeleteMapping("/likes/{userId}/{postId}")
    public String unlikePost(@PathVariable Long userId, @PathVariable Long postId) {
        return likeService.unlikePost(userId, postId);
    }

    @GetMapping("/likes/count/{postId}")
    public long countLikes(@PathVariable Long postId) {
        return likeService.countLikes(postId);
    }
}
