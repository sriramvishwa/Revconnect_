package com.revconnect.service;

import com.revconnect.entity.Like;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.LikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        post = new Post();
        post.setId(1L);
        post.setContent("Test Post");
    }

    @Test
    void likePost_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        String result = likeService.likePost(1L, 1L);

        assertThat(result).isEqualTo("Post liked successfully");
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void likePost_AlreadyLiked_ReturnsError() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(new Like(user, post)));

        String result = likeService.likePost(1L, 1L);

        assertThat(result).isEqualTo("Post already liked");
    }

    @Test
    void unlikePost_Success() {
        Like like = new Like(user, post);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        String result = likeService.unlikePost(1L, 1L);

        assertThat(result).isEqualTo("Post unliked successfully");
        verify(likeRepository, times(1)).delete(like);
    }
}
