package com.pwdk.grocereach.Auth.Application.Implements;

import com.pwdk.grocereach.Auth.Application.Services.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final StringRedisTemplate redisTemplate;
    private final Duration TTL = Duration.ofHours(1);

    private String getRedisKey(String token) {
        return "verify:active:" + token;
    }

    @Override
    public void storeToken(String token) {
        redisTemplate.opsForValue().set(getRedisKey(token), "1", TTL);
    }

    @Override
    public boolean isTokenUsedOrExpired(String token) {
        return redisTemplate.opsForValue().get(getRedisKey(token)) == null;
    }

    @Override
    public void invalidateToken(String token) {
        redisTemplate.delete(getRedisKey(token));
    }
}
