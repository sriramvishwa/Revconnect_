package com.revconnect.service;

import com.revconnect.entity.User;

import java.util.List;

public interface FollowService {

    String followUser(Long followerId, Long followingId);

    String unfollowUser(Long followerId, Long followingId);

    List<User> getFollowers(Long userId);

    List<User> getFollowing(Long userId);

    long countFollowers(Long userId);

    long countFollowing(Long userId);

}
