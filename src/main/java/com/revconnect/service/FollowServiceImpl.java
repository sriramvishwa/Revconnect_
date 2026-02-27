package com.revconnect.service;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowServiceImpl(FollowRepository followRepository,
                             UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String followUser(Long followerId, Long followingId) {

        if (followerId.equals(followingId)) {
            return "You cannot follow yourself";
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            return "Already following this user";
        }

        Follow follow = new Follow(follower, following);
        followRepository.save(follow);

        return "User followed successfully";
    }

    @Override
    public String unfollowUser(Long followerId, Long followingId) {

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = followRepository
                .findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("Follow relationship not found"));

        followRepository.delete(follow);

        return "User unfollowed successfully";
    }

    @Override
    public List<User> getFollowers(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.findByFollowing(user)
                .stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFollowing(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.findByFollower(user)
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
    }

    @Override
    public long countFollowers(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.countByFollowing(user);
    }

    @Override
    public long countFollowing(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.countByFollower(user);
    }
}
