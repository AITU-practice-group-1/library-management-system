package com.example.librarymanagementsystem.Entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//currently not a database entity
@Getter
public class ActiveSession {
    private final String username;
    private final String sessionId;
    private final String deviceInfo;
    @Setter
    private LocalDateTime lastActive;

    public ActiveSession(String username, String sessionId, String deviceInfo, LocalDateTime lastActive) {
        this.username = username;
        this.sessionId = sessionId;
        this.deviceInfo = deviceInfo;
        this.lastActive = lastActive;
    }

}
