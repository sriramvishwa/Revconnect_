package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setUsername("author");
        author.setEmail("author@example.com");
        author.setPassword("password");
        author.setRole(UserRole.PERSONAL);
        entityManager.persist(author);

        post = new Post();
        post.setContent("This is a test post");
        post.setAuthor(author);
        entityManager.persist(post);
        entityManager.flush();
    }

    @Test
    void whenFindByAuthor_thenReturnPosts() {
        List<Post> found = postRepository.findByAuthor(author);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getContent()).isEqualTo(post.getContent());
    }

    @Test
    void whenFindByAuthorInOrderByCreatedAtDesc_thenReturnPageOfPosts() {
        Page<Post> found = postRepository.findByAuthorInOrderByCreatedAtDesc(
                Arrays.asList(author), PageRequest.of(0, 10));

        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getAuthor().getUsername()).isEqualTo("author");
    }
}
