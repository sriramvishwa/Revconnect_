package com.revconnect.controller;

import com.revconnect.entity.Post;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.service.MessageService;
import com.revconnect.service.PostService;
import com.revconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void createPost_ReturnsCreatedPost() throws Exception {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Test content");

        when(postService.createPost(anyLong(), anyString())).thenReturn(post);

        mockMvc.perform(post("/posts/1")
                .content("Test content")
                .contentType(MediaType.TEXT_PLAIN)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    @WithMockUser
    void getUserPosts_ReturnsList() throws Exception {
        when(postService.getPostsByUser(anyLong())).thenReturn(List.of(new Post()));

        mockMvc.perform(get("/posts/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
