package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.ConnectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;

import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    // CREATE POST
    @Override
    public Post createPost(Long userId, String content) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();

        post.setAuthor(author);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // EDIT POST
    @Override
    public Post editPost(Long postId, Long userId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can edit only your posts");
        }

        post.setContent(content);

        return postRepository.save(post);
    }

    // DELETE POST
    @Override
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can delete only your posts");
        }

        postRepository.delete(post);
    }

    // GET USER POSTS
    @Override
    public List<Post> getPostsByUser(Long userId) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByAuthor(author);
    }

    // GET FEED (connections posts)
    @Override
    public Page<Post> getFeed(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get accepted connections (sent)
        List<Connection> sentConnections = connectionRepository.findBySenderAndStatus(user, ConnectionStatus.ACCEPTED);

        // Get accepted connections (received)
        List<Connection> receivedConnections = connectionRepository.findByReceiverAndStatus(user,
                ConnectionStatus.ACCEPTED);

        // Extract users
        List<User> connections = new ArrayList<>();

        for (Connection connection : sentConnections) {
            connections.add(connection.getReceiver());
        }

        for (Connection connection : receivedConnections) {
            connections.add(connection.getSender());
        }

        // include own posts
        connections.add(user);

        return postRepository.findByAuthorInOrderByCreatedAtDesc(
                connections,
                PageRequest.of(page, size));
    }

    @Override
    public Post schedulePost(Long userId, String content, LocalDateTime scheduledAt) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setScheduledAt(scheduledAt);
        post.setCreatedAt(scheduledAt); // Set creation time to scheduled time for sorting later
        return postRepository.save(post);
    }

    @Override
    public Post pinPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can pin only your posts");
        }

        // Unpin others first (optional, but usually one pinned post)
        List<Post> userPosts = postRepository.findByAuthor(post.getAuthor());
        for (Post p : userPosts) {
            if (p.isPinned()) {
                p.setPinned(false);
                postRepository.save(p);
            }
        }

        post.setPinned(true);
        return postRepository.save(post);
    }

    @Override
    public Post repost(Long userId, Long originalPostId, String comment) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post original = postRepository.findById(originalPostId)
                .orElseThrow(() -> new RuntimeException("Original post not found"));

        Post repost = new Post();
        repost.setAuthor(author);
        repost.setOriginalPost(original);
        repost.setContent(comment);
        repost.setCreatedAt(LocalDateTime.now());

        original.setShareCount(original.getShareCount() + 1);
        postRepository.save(original);

        return postRepository.save(repost);
    }

    @Override
    public List<Post> getPostsByUserWithPinned(Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Post> posts = postRepository.findByAuthor(author);
        // Custom sort: pinned first, then by date desc
        posts.sort((p1, p2) -> {
            if (p1.isPinned() && !p2.isPinned())
                return -1;
            if (!p1.isPinned() && p2.isPinned())
                return 1;
            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
        });
        return posts;
    }

}
