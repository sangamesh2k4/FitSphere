package com.sangamesh.Fitsphere.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangamesh.Fitsphere.exception.ApiErrorResponse;
import com.sangamesh.Fitsphere.service.UsageTrackingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final RateLimitService rateLimitService;
    private final UsageTrackingService usageTrackingService;


    private void writeRateLimitResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String message) throws IOException {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .success(false)
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(error));

        response.getWriter().flush();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String key;
        int limit;
        if (uri.startsWith("/api/auth/login")) {
            usageTrackingService.track("login");
            key = "login:" + ip;
            limit = RateLimits.LOGIN_LIMIT;
            if (!rateLimitService.allowRequest(
                    key,
                    limit,
                    RateLimits.LOGIN_WINDOW)) {
                writeRateLimitResponse(request, response, "Too many login attempts.");
                return;

            }

        } else if (uri.startsWith("/api/auth/register")) {
            usageTrackingService.track("register");

            key = "register:" + ip;
            if (!rateLimitService.allowRequest(key, RateLimits.REGISTER_LIMIT, RateLimits.REGISTER_WINDOW)) {
                writeRateLimitResponse(request,response, "Too many registration attempts.");
                return;
            }

        } else if (uri.startsWith("/api/nutrition/search")) {
            usageTrackingService.track("nutrition");
            key = "nutrition:" + ip;
            if (!rateLimitService.allowRequest(key, RateLimits.NUTRITION_LIMIT, RateLimits.API_WINDOW)) {
                writeRateLimitResponse(request,response, "Nutrition search rate limit exceeded.");

                return;
            }
        }
        else  if (uri.startsWith("/api/youtube/search")){
            usageTrackingService.track("youtube");
            key = "youtube:" + ip;
            if (!rateLimitService.allowRequest(key, RateLimits.YOUTUBE_LIMIT , RateLimits.API_WINDOW)) {
                writeRateLimitResponse(request,response, "YouTube API rate limit exceeded.");
                return;
            }}
        filterChain.doFilter(request, response);
    }}
