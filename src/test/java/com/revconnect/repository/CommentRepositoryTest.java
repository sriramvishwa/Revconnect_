package com.revconnect.repository;

import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(UserRole.PERSONAL);
        entityManager.persist(user);

        post = new Post();
        post.setContent("Test Post");
        post.setAuthor(user);
        entityManager.persist(post);

        comment = new Comment();
        comment.setContent("Test Comment");
        comment.setAuthor(user);
        comment.setPost(post);
        entityManager.persist(comment);

        entityManager.flush();
    }

    @Test
    void findByPost_ReturnsComments() {
        List<Comment> comments = commentRepository.findByPost(post);
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("Test Comment");
    }

    @Test
    void findByPostOrderByCreatedAtAsc_ReturnsOrderedComments() {
        Comment secondComment = new Comment();
        secondComment.setContent("Second Comment");
        secondComment.setAuthor(user);
        secondComment.setPost(post);
        entityManager.persist(secondComment);
        entityManager.flush();

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("Test Comment");
        assertThat(comments.get(1).getContent()).isEqualTo("Second Comment");
    }

    @Test
    void countByPost_ReturnsCount() {
        long count = commentRepository.countByPost(post);
        assertThat(count).isEqualTo(1);
    }
}
