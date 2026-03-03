package com.revconnect.repository;

import com.revconnect.entity.Like;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Post post;

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

        entityManager.flush();
    }

    @Test
    void findByUserAndPost_ReturnsLike() {
        Like like = new Like(user, post);
        entityManager.persist(like);
        entityManager.flush();

        Optional<Like> found = likeRepository.findByUserAndPost(user, post);
        assertThat(found).isPresent();
    }

    @Test
    void countByPost_ReturnsCount() {
        Like like = new Like(user, post);
        entityManager.persist(like);
        entityManager.flush();

        long count = likeRepository.countByPost(post);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void deleteByUserAndPost_DeletesLike() {
        Like like = new Like(user, post);
        entityManager.persist(like);
        entityManager.flush();

        likeRepository.deleteByUserAndPost(user, post);
        entityManager.flush();
        entityManager.clear();

        Optional<Like> found = likeRepository.findByUserAndPost(user, post);
        assertThat(found).isNotPresent();
    }
}
