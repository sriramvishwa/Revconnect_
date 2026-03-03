package com.revconnect.controller;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(UserRole.PERSONAL);

        post = new Post();
        post.setId(1L);
        post.setAuthor(user);
        post.setCommentCount(0);
    }

    @Test
    @WithMockUser(username = "testuser")
    void listComments_ReturnsJson() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostOrderByCreatedAtAsc(post)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/comment/1/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_Success_RedirectsToFeed() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        mockMvc.perform(post("/comment/add/1")
                .param("content", "Test Comment")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
    }
}
