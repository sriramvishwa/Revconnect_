package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");
    }

    @Test
    void getCreatorAnalytics_ReturnsCorrectStats() {
        Post post = new Post();
        post.setLikeCount(10);
        post.setCommentCount(5);
        post.setShareCount(2);
        post.setViewCount(100);

        when(postRepository.findByAuthor(creator)).thenReturn(List.of(post));

        Map<String, Object> stats = analyticsService.getCreatorAnalytics(creator);

        assertThat(stats.get("totalLikes")).isEqualTo(10L);
        assertThat(stats.get("totalComments")).isEqualTo(5L);
        assertThat(stats.get("totalShares")).isEqualTo(2L);
        assertThat(stats.get("totalViews")).isEqualTo(100L);
        assertThat(stats.get("engagementRate")).isEqualTo("17.00%");
    }

    @Test
    void getCreatorAnalytics_NoPosts_ReturnsZeroStats() {
        when(postRepository.findByAuthor(creator)).thenReturn(Collections.emptyList());

        Map<String, Object> stats = analyticsService.getCreatorAnalytics(creator);

        assertThat(stats.get("totalLikes")).isEqualTo(0L);
        assertThat(stats.get("engagementRate")).isEqualTo("0.00%");
    }
}
