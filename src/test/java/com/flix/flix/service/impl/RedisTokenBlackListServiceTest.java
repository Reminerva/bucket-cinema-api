package com.flix.flix.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisTokenBlackListServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate; // Mock the main Redis template

    @Mock
    private ValueOperations<String, String> valueOperations; // Mock the opsForValue() result

    @InjectMocks
    private RedisTokenBlackListService redisTokenBlackListService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void blackListToken_shouldCallSetWithCorrectParameters() {
        String token = "test_token_123";
        Long expirationTime = 3600000L; // 1 hour in milliseconds

        redisTokenBlackListService.blackListToken(token, expirationTime);

        verify(valueOperations, times(1)).set(
                eq(token),
                eq("blackListed"),
                eq(expirationTime),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void blackListToken_shouldHandleZeroExpirationTime() {
        String token = "short_lived_token";
        Long expirationTime = 0L;

        redisTokenBlackListService.blackListToken(token, expirationTime);

        verify(valueOperations, times(1)).set(
                eq(token),
                eq("blackListed"),
                eq(expirationTime),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void blackListToken_shouldHandleNegativeExpirationTime() {
        String token = "expired_token";
        Long expirationTime = -1000L;

        redisTokenBlackListService.blackListToken(token, expirationTime);

        verify(valueOperations, times(1)).set(
                eq(token),
                eq("blackListed"),
                eq(expirationTime),
                eq(TimeUnit.MILLISECONDS)
        );
    }
}