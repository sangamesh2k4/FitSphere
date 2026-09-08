package com.sangamesh.Fitsphere.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RateLimitService rateLimitService;

    private final String key = "login:127.0.0.1";
    private final int maxRequests = 5;
    private final Duration window = Duration.ofMinutes(1);

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
    }

    @Test
    void allowRequest_returnsTrueWhenWithinLimit() {

        when(valueOperations.increment(key))
                .thenReturn(3L);

        boolean result =
                rateLimitService.allowRequest(
                        key,
                        maxRequests,
                        window
                );

        assertTrue(result);

        verify(valueOperations).increment(key);
        verify(redisTemplate, never())
                .expire(anyString(), any(Duration.class));
    }

    @Test
    void allowRequest_returnsTrueWhenExactlyAtLimit() {

        when(valueOperations.increment(key))
                .thenReturn(5L);

        boolean result =
                rateLimitService.allowRequest(
                        key,
                        maxRequests,
                        window
                );

        assertTrue(result);

        verify(valueOperations).increment(key);
    }

    @Test
    void allowRequest_returnsFalseWhenLimitExceeded() {

        when(valueOperations.increment(key))
                .thenReturn(6L);

        boolean result =
                rateLimitService.allowRequest(
                        key,
                        maxRequests,
                        window
                );

        assertFalse(result);

        verify(valueOperations).increment(key);
    }

    @Test
    void allowRequest_setsExpirationWhenFirstRequest() {

        when(valueOperations.increment(key))
                .thenReturn(1L);

        boolean result =
                rateLimitService.allowRequest(
                        key,
                        maxRequests,
                        window
                );

        assertTrue(result);

        verify(redisTemplate)
                .expire(key, window);
    }

    @Test
    void allowRequest_doesNotSetExpirationAfterFirstRequest() {

        when(valueOperations.increment(key))
                .thenReturn(2L);

        rateLimitService.allowRequest(
                key,
                maxRequests,
                window
        );

        verify(redisTemplate, never())
                .expire(anyString(), any(Duration.class));
    }

    @Test
    void allowRequest_returnsFalseWhenRedisReturnsNull() {

        when(valueOperations.increment(key))
                .thenReturn(null);

        boolean result =
                rateLimitService.allowRequest(
                        key,
                        maxRequests,
                        window
                );

        assertFalse(result);

        verify(redisTemplate, never())
                .expire(anyString(), any(Duration.class));
    }
}