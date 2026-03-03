package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MessageService messageService;

    @MockBean
    private FollowService followService;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(UserRole.PERSONAL);
        user.setPrivate(false);
    }

    @Test
    @WithMockUser(username = "testuser")
    void viewProfile_ReturnsProfileView() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(postService.getPostsByUserWithPinned(anyLong())).thenReturn(Collections.emptyList());
        when(messageService.countUnreadMessages(any())).thenReturn(0L);
        when(notificationRepository.countByRecipientAndIsReadFalse(any())).thenReturn(0L);
        when(followService.isFollowing(anyLong(), anyLong())).thenReturn(false);

        mockMvc.perform(get("/profile/testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profileUser", "currentUser", "posts", "activeTab"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void editProfile_ReturnsEditView() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"));
    }
}
