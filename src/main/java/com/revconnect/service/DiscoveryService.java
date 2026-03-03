package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiscoveryService {

    @Autowired
    private PostRepository postRepository;

    public List<String> getTrendingHashtags() {
        List<Post> recentPosts = postRepository.findAll(); // In a real app, filter for last 24h
        Map<String, Long> hashtagCounts = recentPosts.stream()
                .flatMap(p -> p.getHashtags().stream())
                .collect(Collectors.groupingBy(h -> h, Collectors.counting()));

        return hashtagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Post> searchPostsByHashtag(String hashtag) {
        // Simple implementation: filter all posts. Real app would use a DB query.
        return postRepository.findAll().stream()
                .filter(p -> p.getHashtags().contains(hashtag))
                .collect(Collectors.toList());
    }
}
