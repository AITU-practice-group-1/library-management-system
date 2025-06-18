package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.Entities.ActiveSession;
import com.example.librarymanagementsystem.Services.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheSessionStore implements SessionStore {
    private final RedisTemplate<String, ActiveSession> redisTemplate;

    @Value("${session.expHrs}")
    private int expirationHours;
    @Value("${session.expMins}")
    private int expirationMins;

    @Autowired
    public RedisCacheSessionStore(RedisTemplate<String, ActiveSession> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void registerNewSession(String username, String sessionId, String deviceInfo) {
        // Invalidate existing sessions for the user
        String keyPattern = username + ":*";
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        // Store new session
        ActiveSession session = new ActiveSession(username, sessionId, deviceInfo, LocalDateTime.now());
        String key = username + ":" + sessionId;
        redisTemplate.opsForValue().set(key, session, expirationMins + expirationHours * 60L, TimeUnit.MINUTES);
    }

    @Override
    public boolean isValidSession(String sessionId) {
        String keyPattern  = "*:" + sessionId;
        Set<String> keys = redisTemplate.keys(keyPattern);
        return keys != null && !keys.isEmpty();
    }

    @Override
    public void updateLastActive(String sessionId) {
        String keyPattern = "*:" + sessionId;
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys == null || keys.isEmpty()) {
            return;
        }
        String key = keys.iterator().next();
        ActiveSession session = redisTemplate.opsForValue().get(key);
        if (session != null) {
            session.setLastActive(LocalDateTime.now());
            redisTemplate.opsForValue().set(key, session, expirationMins + expirationHours * 60L, TimeUnit.MINUTES);
        }
    }

    @Override
    public void invalidateSession(String sessionId) {
        String keyPattern = "*:" + sessionId;
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
