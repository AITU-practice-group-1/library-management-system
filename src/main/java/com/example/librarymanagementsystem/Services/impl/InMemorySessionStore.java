package com.example.librarymanagementsystem.Services.impl;

import com.example.librarymanagementsystem.Entities.ActiveSession;
import com.example.librarymanagementsystem.Services.SessionStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class InMemorySessionStore implements SessionStore {

    private final Map<String, ActiveSession> activeSessions = new ConcurrentHashMap<>();

    @Value("${session.expHrs:24}")
    private int expirationHours;
    @Value("${session.expMins:0}")
    private int expirationMins;

    public void registerNewSession(String username, String sessionId, String deviceInfo) {
        // Invalidate existing sessions for the user
        activeSessions.entrySet().removeIf(entry -> entry.getValue().getUsername().equals(username));
        // Store new session
        ActiveSession session = new ActiveSession(username, sessionId, deviceInfo, LocalDateTime.now());
        activeSessions.put(sessionId, session);
    }

    public boolean isValidSession(String sessionId) {
        return activeSessions.containsKey(sessionId) && !isSessionExpired(sessionId);
    }

    public void updateLastActive(String sessionId) {
        ActiveSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.setLastActive(LocalDateTime.now());
        }
    }

    private boolean isSessionExpired(String sessionId) {
        ActiveSession session = activeSessions.get(sessionId);
        return session != null && session.getLastActive().plusHours(expirationHours).plusMinutes(expirationMins).isBefore(LocalDateTime.now());
    }

    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}