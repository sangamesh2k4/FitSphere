package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.analytics.DashboardAnalyticsDto;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.AnalyticsService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UsageTrackingService usageTrackingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getDashboardAnalytics_shouldReturn200() throws Exception {

        DashboardAnalyticsDto dto = DashboardAnalyticsDto.builder()
                .currentWeight(80.0)
                .weightChange(-1.0)
                .currentBodyFatPercentage(18.0)
                .bodyFatChange(-0.5)
                .averageDailyCalories(2000.0)
                .recommendedCalories(2200.0)
                .averageDailyProtein(150.0)
                .recommendedProtein(160.0)
                .calorieTargetPercentage(90.91)
                .proteinTargetPercentage(93.75)
                .daysLogged(2L)
                .build();

        when(analyticsService.getDashboardAnalytics())
                .thenReturn(dto);

        mockMvc.perform(get("/api/analytics/dashboard"))
                .andExpect(status().isOk());

        verify(analyticsService).getDashboardAnalytics();
    }
}