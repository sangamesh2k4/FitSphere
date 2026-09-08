package com.sangamesh.Fitsphere.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private UsageTrackingService usageTrackingService;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        rateLimitFilter = new RateLimitFilter(
                objectMapper,
                rateLimitService,
                usageTrackingService
        );

        request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        response = new MockHttpServletResponse();
    }

    @Test
    void loginRequest_allowed_shouldContinueFilterChain() throws Exception {

        request.setRequestURI("/api/auth/login");

        when(rateLimitService.allowRequest(
                eq("login:127.0.0.1"),
                eq(RateLimits.LOGIN_LIMIT),
                eq(RateLimits.LOGIN_WINDOW)
        )).thenReturn(true);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("login");

        verify(rateLimitService).allowRequest(
                "login:127.0.0.1",
                RateLimits.LOGIN_LIMIT,
                RateLimits.LOGIN_WINDOW
        );

        verify(filterChain).doFilter(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    void loginRequest_rateLimited_shouldReturn429() throws Exception {

        request.setRequestURI("/api/auth/login");

        when(rateLimitService.allowRequest(
                eq("login:127.0.0.1"),
                eq(RateLimits.LOGIN_LIMIT),
                eq(RateLimits.LOGIN_WINDOW)
        )).thenReturn(false);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("login");

        verify(rateLimitService).allowRequest(
                "login:127.0.0.1",
                RateLimits.LOGIN_LIMIT,
                RateLimits.LOGIN_WINDOW
        );

        verify(filterChain, never()).doFilter(any(), any());

        assertEquals(429, response.getStatus());
        assertEquals("application/json", response.getContentType());

        assertTrue(response.getContentAsString()
                .contains("Too many login attempts."));
    }

    @Test
    void registerRequest_allowed_shouldContinueFilterChain() throws Exception {

        request.setRequestURI("/api/auth/register");

        when(rateLimitService.allowRequest(
                eq("register:127.0.0.1"),
                eq(RateLimits.REGISTER_LIMIT),
                eq(RateLimits.REGISTER_WINDOW)
        )).thenReturn(true);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("register");

        verify(filterChain).doFilter(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    void registerRequest_rateLimited_shouldReturn429() throws Exception {

        request.setRequestURI("/api/auth/register");

        when(rateLimitService.allowRequest(
                eq("register:127.0.0.1"),
                eq(RateLimits.REGISTER_LIMIT),
                eq(RateLimits.REGISTER_WINDOW)
        )).thenReturn(false);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("register");

        verify(filterChain, never()).doFilter(any(), any());

        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString()
                .contains("Too many registration attempts."));
    }

    @Test
    void nutritionRequest_allowed_shouldContinueFilterChain() throws Exception {

        request.setRequestURI("/api/nutrition/search");

        when(rateLimitService.allowRequest(
                eq("nutrition:127.0.0.1"),
                eq(RateLimits.NUTRITION_LIMIT),
                eq(RateLimits.API_WINDOW)
        )).thenReturn(true);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("nutrition");

        verify(filterChain).doFilter(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    void nutritionRequest_rateLimited_shouldReturn429() throws Exception {

        request.setRequestURI("/api/nutrition/search");

        when(rateLimitService.allowRequest(
                eq("nutrition:127.0.0.1"),
                eq(RateLimits.NUTRITION_LIMIT),
                eq(RateLimits.API_WINDOW)
        )).thenReturn(false);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("nutrition");

        verify(filterChain, never()).doFilter(any(), any());

        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString()
                .contains("Nutrition search rate limit exceeded."));
    }

    @Test
    void youtubeRequest_allowed_shouldContinueFilterChain() throws Exception {

        request.setRequestURI("/api/youtube/search");

        when(rateLimitService.allowRequest(
                eq("youtube:127.0.0.1"),
                eq(RateLimits.YOUTUBE_LIMIT),
                eq(RateLimits.API_WINDOW)
        )).thenReturn(true);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("youtube");

        verify(filterChain).doFilter(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    void youtubeRequest_rateLimited_shouldReturn429() throws Exception {

        request.setRequestURI("/api/youtube/search");

        when(rateLimitService.allowRequest(
                eq("youtube:127.0.0.1"),
                eq(RateLimits.YOUTUBE_LIMIT),
                eq(RateLimits.API_WINDOW)
        )).thenReturn(false);

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(usageTrackingService).track("youtube");

        verify(filterChain, never()).doFilter(any(), any());

        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString()
                .contains("YouTube API rate limit exceeded."));
    }

    @Test
    void unrelatedRequest_shouldContinueWithoutRateLimitCheck() throws Exception {

        request.setRequestURI("/api/exercises");

        rateLimitFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verifyNoInteractions(
                rateLimitService,
                usageTrackingService
        );

        verify(filterChain).doFilter(request, response);

        assertEquals(200, response.getStatus());
    }
}