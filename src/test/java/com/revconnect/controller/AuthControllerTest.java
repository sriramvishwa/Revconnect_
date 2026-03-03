package com.revconnect.controller;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.service.MessageService;
import com.revconnect.service.PostService;
import com.revconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    void login_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void register_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void feed_Authenticated_ReturnsFeedView() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(com.revconnect.entity.UserRole.PERSONAL);

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(postService.getFeed(anyLong(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of()));
        when(notificationRepository.countByRecipientAndIsReadFalse(user)).thenReturn(0L);
        when(messageService.countUnreadMessages(user)).thenReturn(0L);

        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("user", "posts", "unreadCount", "unreadMessageCount"));
    }

    @Test
    void feed_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/feed"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void registerUser_Success_RedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("email", "new@example.com")
                .param("password", "password")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }
}
