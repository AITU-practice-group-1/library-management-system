package com.example.librarymanagementsystem.Services;

public interface SessionStore {
    void registerNewSession(String username, String sessionId, String deviceInfo);
    boolean isValidSession(String sessionId);
    void updateLastActive(String sessionId);
    void invalidateSession(String sessionId);
}
