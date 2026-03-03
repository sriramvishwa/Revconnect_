package com.revconnect.service;

import com.revconnect.entity.Like;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.LikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;


    // LIKE POST
    @Override
    public String likePost(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (likeRepository.findByUserAndPost(user, post).isPresent()) {
            return "Post already liked";
        }

        Like like = new Like(user, post);

        likeRepository.save(like);

        return "Post liked successfully";
    }


    // UNLIKE POST
    @Override
    public String unlikePost(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);

        return "Post unliked successfully";
    }


    // COUNT LIKES
    @Override
    public long countLikes(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return likeRepository.countByPost(post);
    }

}
