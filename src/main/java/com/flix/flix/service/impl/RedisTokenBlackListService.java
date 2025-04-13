package com.flix.flix.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisTokenBlackListService {

    private final StringRedisTemplate stringRedisTemplate;

    public void blackListToken(String token, Long expirationTime){

        stringRedisTemplate.opsForValue().set(token, "blackListed", expirationTime, TimeUnit.MILLISECONDS);

    }

    public Boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
    }
}
