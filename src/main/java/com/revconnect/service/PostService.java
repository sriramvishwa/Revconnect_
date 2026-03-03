package com.revconnect.service;

import com.revconnect.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    Post createPost(Long userId, String content);

    Post editPost(Long postId, Long userId, String content);

    void deletePost(Long postId, Long userId);

    List<Post> getPostsByUser(Long userId);

    Page<Post> getFeed(Long userId, int page, int size);

    // New Features
    Post schedulePost(Long userId, String content, java.time.LocalDateTime scheduledAt);

    Post pinPost(Long postId, Long userId);

    Post repost(Long userId, Long originalPostId, String comment);

    List<Post> getPostsByUserWithPinned(Long userId);

    Post save(Post post);
}
