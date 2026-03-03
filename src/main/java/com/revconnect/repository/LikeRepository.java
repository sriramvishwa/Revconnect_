package com.revconnect.repository;

import com.revconnect.entity.Like;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    void deleteByUserAndPost(User user, Post post);
}
