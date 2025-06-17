package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Entities.ActiveSession;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionStore {

    private final Map<String, ActiveSession> activeSessions = new ConcurrentHashMap<>();

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
        return session != null && session.getLastActive().plusHours(24).isBefore(LocalDateTime.now());
    }

    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
