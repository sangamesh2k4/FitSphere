package com.sangamesh.Fitsphere.controller;

import com.sangamesh.Fitsphere.dto.youtube.YoutubeVideoDto;
import com.sangamesh.Fitsphere.ratelimit.RateLimitService;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import com.sangamesh.Fitsphere.security.JwtService;
import com.sangamesh.Fitsphere.service.CustomUserDetailsService;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import com.sangamesh.Fitsphere.service.YoutubeRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(YoutubeController.class)
@AutoConfigureMockMvc(addFilters = false)
class YoutubeControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private YoutubeRecommendationService youtubeRecommendationService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UsageTrackingService usageTrackingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void search_shouldReturnVideos() throws Exception {

        YoutubeVideoDto video = YoutubeVideoDto.builder()
                .videoId("abc123")
                .title("Bench Press Tutorial")
                .channelTitle("Fitness Channel")
                .thumbnailUrl("https://example.com/thumb.jpg")
                .videoUrl("https://www.youtube.com/watch?v=abc123")
                .build();

        when(youtubeRecommendationService.searchTutorials("Bench Press"))
                .thenReturn(List.of(video));

        mockMvc.perform(get("/api/youtube/search")
                        .param("exercise", "Bench Press"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].videoId").value("abc123"))
                .andExpect(jsonPath("$[0].title")
                        .value("Bench Press Tutorial"))
                .andExpect(jsonPath("$[0].channelTitle")
                        .value("Fitness Channel"))
                .andExpect(jsonPath("$[0].thumbnailUrl")
                        .value("https://example.com/thumb.jpg"))
                .andExpect(jsonPath("$[0].videoUrl")
                        .value("https://www.youtube.com/watch?v=abc123"));

        verify(youtubeRecommendationService)
                .searchTutorials("Bench Press");
    }

    @Test
    void search_shouldReturnEmptyList() throws Exception {

        when(youtubeRecommendationService.searchTutorials("Squat"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/youtube/search")
                        .param("exercise", "Squat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(youtubeRecommendationService)
                .searchTutorials("Squat");
    }

    @Test
    void search_whenExerciseMissing_shouldReturn400() throws Exception {

        mockMvc.perform(get("/api/youtube/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(youtubeRecommendationService);
    }

    @Test
    void search_shouldPassExerciseParameterCorrectly() throws Exception {

        when(youtubeRecommendationService.searchTutorials("deadlift"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/youtube/search")
                        .param("exercise", "deadlift"))
                .andExpect(status().isOk());

        verify(youtubeRecommendationService)
                .searchTutorials("deadlift");
    }
}