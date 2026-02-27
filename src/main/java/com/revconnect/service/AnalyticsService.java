package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private PostRepository postRepository;

    public Map<String, Object> getCreatorAnalytics(User creator) {
        List<Post> posts = postRepository.findByAuthor(creator);

        long totalLikes = posts.stream().mapToLong(Post::getLikeCount).sum();
        long totalComments = posts.stream().mapToLong(Post::getCommentCount).sum();
        long totalShares = posts.stream().mapToLong(Post::getShareCount).sum();
        long totalViews = posts.stream().mapToLong(Post::getViewCount).sum();

        double engagementRate = totalViews > 0
                ? ((double) (totalLikes + totalComments + totalShares) / totalViews) * 100
                : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLikes", totalLikes);
        stats.put("totalComments", totalComments);
        stats.put("totalShares", totalShares);
        stats.put("totalViews", totalViews);
        stats.put("engagementRate", String.format("%.2f%%", engagementRate));
        stats.put("postsCount", posts.size());

        return stats;
    }
}
