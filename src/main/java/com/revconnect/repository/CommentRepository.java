package com.revconnect.repository;

import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    long countByPost(Post post);
}
