package com.revconnect.controller;

import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.MessageService;
import com.revconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "testuser")
    void notificationsPage_ReturnsView() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(com.revconnect.entity.UserRole.PERSONAL);

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(user)).thenReturn(List.of());
        when(messageService.countUnreadMessages(user)).thenReturn(0L);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"))
                .andExpect(model().attributeExists("user", "notifications", "unreadCount", "unreadMessageCount"));
    }

    @Test
    @WithMockUser
    void getNotifications_ReturnsJson() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(user)).thenReturn(List.of());

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void markAsRead_ReturnsOk() throws Exception {
        Notification notification = new Notification();
        notification.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        mockMvc.perform(post("/notifications/read/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void markAllRead_ReturnsOk() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(user)).thenReturn(List.of());

        mockMvc.perform(post("/notifications/read-all")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
