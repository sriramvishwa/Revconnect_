package com.revconnect.repository;

import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(UserRole.PERSONAL);
        user.setFirstName("Test");
        user.setLastName("User");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void whenSearchUsers_thenReturnMatchingUsers() {
        List<User> results = userRepository.searchUsers("test");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void whenSearchUsersByFirstName_thenReturnMatchingUsers() {
        List<User> results = userRepository.searchUsers("Test");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Test");
    }
}
