package com.revconnect.controller;

import com.revconnect.repository.PostRepository;
import com.revconnect.service.LikeService;
import com.revconnect.service.NotificationService;
import com.revconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void countLikes_ReturnsCount() throws Exception {
        when(likeService.countLikes(1L)).thenReturn(5L);

        mockMvc.perform(get("/likes/count/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @WithMockUser
    void likePost_ReturnsMessage() throws Exception {
        when(likeService.likePost(1L, 1L)).thenReturn("Liked");

        mockMvc.perform(post("/likes/1/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Liked"));
    }
}
