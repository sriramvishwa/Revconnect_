package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import com.revconnect.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NetworkController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class NetworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private FollowService followService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(UserRole.PERSONAL);
    }

    @Test
    @WithMockUser(username = "testuser")
    void network_ReturnsNetworkView() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(connectionService.getConnections(1L)).thenReturn(Collections.emptyList());
        when(connectionService.getPendingRequests(1L)).thenReturn(Collections.emptyList());
        when(followService.getFollowers(1L)).thenReturn(Collections.emptyList());
        when(followService.getFollowing(1L)).thenReturn(Collections.emptyList());
        when(messageService.countUnreadMessages(user)).thenReturn(0L);

        mockMvc.perform(get("/network"))
                .andExpect(status().isOk())
                .andExpect(view().name("network"))
                .andExpect(model().attributeExists("user", "connections", "pendingRequests", "followers", "following"));
    }
}
