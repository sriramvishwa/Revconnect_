package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import com.revconnect.service.DiscoveryService;
import com.revconnect.service.UserService;
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

@WebMvcTest(SearchController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscoveryService discoveryService;

    @MockBean
    private UserService userService;

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
    void search_Users_ReturnsResults() throws Exception {
        when(userService.searchUsers("test")).thenReturn(Collections.singletonList(user));
        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/search").param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attribute("type", "USERS"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void search_Hashtag_ReturnsResults() throws Exception {
        when(discoveryService.searchPostsByHashtag("test")).thenReturn(Collections.emptyList());
        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/search").param("q", "#test"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attribute("type", "POSTS"));
    }
}
