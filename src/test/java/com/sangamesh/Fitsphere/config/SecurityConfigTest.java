package com.sangamesh.Fitsphere.config;

import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UsageTrackingService usageTrackingService;


    @BeforeEach
    void setUp() {
        when(rateLimitService.allowRequest(
                anyString(),
                anyInt(),
                any(Duration.class)
        )).thenReturn(true);
    }

    @Test
    void authEndpoints_arePublic() throws Exception {

        mockMvc.perform(
                        options("/api/auth/login")
                )
                .andExpect(status().isOk());
    }


    @Test
    void exerciseEndpoints_arePublic() throws Exception {

        mockMvc.perform(
                        get("/api/exercises")
                )
                .andExpect(status().isOk());
    }


    @Test
    void youtubeEndpoints_arePublic() throws Exception {

        mockMvc.perform(
                        get("/api/youtube/search")
                                .param("exercise", "bench press")
                )
                .andExpect(status().isOk());
    }


    @Test
    void contactEndpoint_isPublic() throws Exception {

        mockMvc.perform(
                        post("/api/contact")
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void foodLogEndpoint_requiresAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/foodlogs/today")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void profileEndpoint_requiresAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/profile")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void measurementsEndpoint_requiresAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/measurements")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void accountEndpoint_requiresAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/account")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void adminEndpoint_requiresAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/admin/usage")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoint_rejectsRegularUser() throws Exception {

        mockMvc.perform(
                        get("/api/admin/usage")
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_allowsAdmin() throws Exception {

        mockMvc.perform(
                        get("/api/admin/usage")
                )
                .andExpect(status().isOk());
    }


    @Test
    void unknownProtectedEndpoint_requiresAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/something-that-does-not-exist")
                )
                .andExpect(status().isUnauthorized());
    }

}