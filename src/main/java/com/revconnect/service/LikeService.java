package com.revconnect.service;

public interface LikeService {

    String likePost(Long userId, Long postId);

    String unlikePost(Long userId, Long postId);

    long countLikes(Long postId);

}
