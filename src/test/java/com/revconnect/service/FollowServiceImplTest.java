package com.revconnect.service;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.repository.FollowRepository;
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
public class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        follower = new User();
        follower.setId(1L);
        follower.setUsername("follower");
        follower.setFollowingCount(0);

        following = new User();
        following.setId(2L);
        following.setUsername("following");
        following.setFollowersCount(0);
    }

    @Test
    void followUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);

        String result = followService.followUser(1L, 2L);

        assertThat(result).isEqualTo("User followed successfully");
        assertThat(follower.getFollowingCount()).isEqualTo(1);
        assertThat(following.getFollowersCount()).isEqualTo(1);
        verify(followRepository, times(1)).save(any(Follow.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void followUser_SameUser_ReturnsError() {
        String result = followService.followUser(1L, 1L);
        assertThat(result).isEqualTo("You cannot follow yourself");
    }

    @Test
    void unfollowUser_Success() {
        Follow follow = new Follow(follower, following);
        follower.setFollowingCount(1);
        following.setFollowersCount(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(follow));

        String result = followService.unfollowUser(1L, 2L);

        assertThat(result).isEqualTo("User unfollowed successfully");
        assertThat(follower.getFollowingCount()).isEqualTo(0);
        assertThat(following.getFollowersCount()).isEqualTo(0);
        verify(followRepository, times(1)).delete(follow);
    }
}
