package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("author");

        post = new Post();
        post.setId(1L);
        post.setAuthor(author);
        post.setContent("Original content");
    }

    @Test
    void createPost_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post created = postService.createPost(1L, "New post");

        assertThat(created.getContent()).isEqualTo("New post");
        assertThat(created.getAuthor()).isEqualTo(author);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void editPost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post edited = postService.editPost(1L, 1L, "Updated content");

        assertThat(edited.getContent()).isEqualTo("Updated content");
    }

    @Test
    void editPost_NotAuthor_ThrowsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.editPost(1L, 2L, "Updated content"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can edit only your posts");
    }

    @Test
    void deletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, 1L);

        verify(postRepository).delete(post);
    }
}
