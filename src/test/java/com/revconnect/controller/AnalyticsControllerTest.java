package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import com.revconnect.service.AnalyticsService;
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

@WebMvcTest(AnalyticsController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");
        creator.setRole(UserRole.BUSINESS);
    }

    @Test
    @WithMockUser(username = "creator")
    void showAnalytics_BusinessUser_ReturnsView() throws Exception {
        when(userService.findByUsername("creator")).thenReturn(creator);
        when(analyticsService.getCreatorAnalytics(creator)).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("analytics"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "personal")
    void showAnalytics_PersonalUser_RedirectsToFeed() throws Exception {
        User personal = new User();
        personal.setUsername("personal");
        personal.setRole(UserRole.PERSONAL);

        when(userService.findByUsername("personal")).thenReturn(personal);

        mockMvc.perform(get("/analytics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
    }
}
