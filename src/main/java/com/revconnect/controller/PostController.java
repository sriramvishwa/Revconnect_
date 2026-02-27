package com.revconnect.controller;

import com.revconnect.entity.Post;
import com.revconnect.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;


    // CREATE POST
    @PostMapping("/{userId}")
    public Post createPost(
            @PathVariable Long userId,
            @RequestBody String content) {

        return postService.createPost(userId, content);
    }


    // EDIT POST
    @PutMapping("/{postId}/{userId}")
    public Post editPost(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestBody String content) {

        return postService.editPost(postId, userId, content);
    }


    // DELETE POST
    @DeleteMapping("/{postId}/{userId}")
    public String deletePost(
            @PathVariable Long postId,
            @PathVariable Long userId) {

        postService.deletePost(postId, userId);

        return "Post deleted successfully";
    }


    // GET USER POSTS
    @GetMapping("/user/{userId}")
    public List<Post> getUserPosts(@PathVariable Long userId) {

        return postService.getPostsByUser(userId);
    }


    // GET FEED
    @GetMapping("/feed/{userId}")
    public Page<Post> getFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return postService.getFeed(userId, page, size);
    }

}
