package com.sangamesh.Fitsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.dto.health.HealthAssessmentRequestDto;
import com.sangamesh.Fitsphere.dto.health.HealthAssessmentResponseDto;
import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.HealthAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
@AutoConfigureMockMvc(addFilters = false)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private  RateLimitService rateLimitService;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private HealthAnalysisService healthAnalysisService;

    @Test
    void analyzeHealth_success() throws Exception {

        HealthAssessmentRequestDto request =
                new HealthAssessmentRequestDto();

        HealthAssessmentResponseDto response =
                new HealthAssessmentResponseDto();

        response.setBmi(22.86);
        response.setBmr(1700.0);
        response.setTdee(2635.0);
        response.setRecommendedCalories(2635.0);

        when(healthAnalysisService.analyzeHealth(any(
                HealthAssessmentRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/health/assessment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmi").value(22.86))
                .andExpect(jsonPath("$.bmr").value(1700.0))
                .andExpect(jsonPath("$.tdee").value(2635.0))
                .andExpect(jsonPath("$.recommendedCalories").value(2635.0));

        verify(healthAnalysisService)
                .analyzeHealth(any(HealthAssessmentRequestDto.class));
    }

    @Test
    void analyzeHealth_emptyBody_returnsBadRequest() throws Exception {

        mockMvc.perform(post("/api/health/assessment")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void analyzeHealth_invalidJson_returnsBadRequest() throws Exception {

        mockMvc.perform(post("/api/health/assessment")
                        .contentType("application/json")
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void analyzeHealth_serviceException_propagates() throws Exception {

        HealthAssessmentRequestDto request =
                new HealthAssessmentRequestDto();

        when(healthAnalysisService.analyzeHealth(any(
                HealthAssessmentRequestDto.class)))
                .thenThrow(new RuntimeException("Health analysis failed"));

        mockMvc.perform(post("/api/health/assessment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        verify(healthAnalysisService)
                .analyzeHealth(any(HealthAssessmentRequestDto.class));
    }
}