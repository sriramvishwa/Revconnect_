package com.revconnect.controller;

import com.revconnect.service.ConnectionService;
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

@WebMvcTest(ConnectionController.class)
@org.springframework.context.annotation.Import(com.revconnect.config.SecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
public class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void sendConnectionRequest_ReturnsMessage() throws Exception {
        when(connectionService.sendConnectionRequest(1L, 2L)).thenReturn("Success");

        mockMvc.perform(post("/api/connections/request")
                .param("senderId", "1")
                .param("receiverId", "2")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    @WithMockUser
    void getConnections_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/connections/1"))
                .andExpect(status().isOk());
    }
}
