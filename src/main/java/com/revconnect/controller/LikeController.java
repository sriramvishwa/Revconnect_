package com.revconnect.controller;

import com.revconnect.service.LikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;


    // LIKE POST
    @PostMapping("/{userId}/{postId}")
    public String likePost(
            @PathVariable Long userId,
            @PathVariable Long postId) {

        return likeService.likePost(userId, postId);
    }


    // UNLIKE POST
    @DeleteMapping("/{userId}/{postId}")
    public String unlikePost(
            @PathVariable Long userId,
            @PathVariable Long postId) {

        return likeService.unlikePost(userId, postId);
    }


    // COUNT LIKES
    @GetMapping("/count/{postId}")
    public long countLikes(@PathVariable Long postId) {

        return likeService.countLikes(postId);
    }

}
