package com.revconnect.repository;

import com.revconnect.entity.Follow;
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
public class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        follower = new User();
        follower.setUsername("follower");
        follower.setEmail("follower@example.com");
        follower.setPassword("password");
        follower.setRole(UserRole.PERSONAL);
        entityManager.persist(follower);

        following = new User();
        following.setUsername("following");
        following.setEmail("following@example.com");
        following.setPassword("password");
        following.setRole(UserRole.PERSONAL);
        entityManager.persist(following);

        entityManager.flush();
    }

    @Test
    void findByFollowerAndFollowing_ReturnsFollow() {
        Follow follow = new Follow(follower, following);
        entityManager.persist(follow);
        entityManager.flush();

        Optional<Follow> found = followRepository.findByFollowerAndFollowing(follower, following);
        assertThat(found).isPresent();
    }

    @Test
    void existsByFollowerAndFollowing_ReturnsTrue() {
        Follow follow = new Follow(follower, following);
        entityManager.persist(follow);
        entityManager.flush();

        boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);
        assertThat(exists).isTrue();
    }

    @Test
    void countByFollowing_ReturnsCount() {
        Follow follow = new Follow(follower, following);
        entityManager.persist(follow);
        entityManager.flush();

        long count = followRepository.countByFollowing(following);
        assertThat(count).isEqualTo(1);
    }
}
