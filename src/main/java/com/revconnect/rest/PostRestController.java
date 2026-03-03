package com.revconnect.rest;

import com.revconnect.dto.PostDto;
import com.revconnect.entity.Post;
import com.revconnect.mapper.PostMapper;
import com.revconnect.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final PostMapper postMapper;

    public PostRestController(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping("/feed/{userId}")
    public ResponseEntity<List<PostDto>> getFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> feed = postService.getFeed(userId, page, size);
        List<PostDto> dtos = feed.getContent().stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUser(userId);
        List<PostDto> dtos = posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<PostDto> createPost(
            @PathVariable Long userId,
            @RequestBody String content) {
        Post post = postService.createPost(userId, content);
        return ResponseEntity.ok(postMapper.toDto(post));
    }
}
