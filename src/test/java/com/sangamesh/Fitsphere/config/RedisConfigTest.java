package com.sangamesh.Fitsphere.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RedisConfigTest {

    private final RedisConfig redisConfig =
            new RedisConfig();

    @Test
    void cacheManager_createsRedisCacheManager() {

        RedisConnectionFactory connectionFactory =
                mock(RedisConnectionFactory.class);

        var cacheManager =
                redisConfig.cacheManager(connectionFactory);

        assertNotNull(cacheManager);
        assertInstanceOf(
                RedisCacheManager.class,
                cacheManager
        );
    }

    @Test
    void redisTemplate_createsRedisTemplate() {

        RedisConnectionFactory connectionFactory =
                mock(RedisConnectionFactory.class);

        RedisTemplate<String, Object> template =
                redisConfig.redisTemplate(connectionFactory);

        assertNotNull(template);
        assertSame(
                connectionFactory,
                template.getConnectionFactory()
        );

        assertNotNull(template.getKeySerializer());
        assertNotNull(template.getValueSerializer());
    }

    @Test
    void cacheConfiguration_has24HourTtl() {

        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(java.time.Duration.ofHours(24));

        assertEquals(
                java.time.Duration.ofHours(24),
                configuration.getTtl()
        );
    }
}