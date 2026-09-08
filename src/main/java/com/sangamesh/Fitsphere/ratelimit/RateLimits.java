package com.sangamesh.Fitsphere.ratelimit;

import java.time.Duration;

public final class RateLimits {

    private RateLimits() {
    }

    public static final int LOGIN_LIMIT = 5;
    public static final int REGISTER_LIMIT = 3;
    public static final int NUTRITION_LIMIT = 30;
    public static final int YOUTUBE_LIMIT = 20;
    public static final int DEFAULT_LIMIT = 60;

    public static final Duration LOGIN_WINDOW = Duration.ofMinutes(1);
    public static final Duration REGISTER_WINDOW = Duration.ofHours(1);
    public static final Duration API_WINDOW = Duration.ofMinutes(1);
}