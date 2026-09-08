package com.sangamesh.Fitsphere.service.impl;

import com.sangamesh.Fitsphere.service.UsageTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageTrackingServiceImpl
        implements UsageTrackingService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void track(String feature) {
        String key = "usage:" + feature + ":" + LocalDate.now();
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofDays(30));
        }
    }

    @Override
    public long getTodayUsage(String feature) {
        String key = "usage:" + feature + ":" + LocalDate.now();
        String value = redisTemplate.opsForValue().get(key);

        return value == null ? 0 : Long.parseLong(value);
    }
}
