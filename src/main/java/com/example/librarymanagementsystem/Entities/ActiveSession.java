package com.example.librarymanagementsystem.Entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

//currently not a database entity
@Getter
@Setter
public class ActiveSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String sessionId;
    private final String deviceInfo;
    private LocalDateTime lastActive;

    public ActiveSession() {
        this.username = null;
        this.sessionId = null;
        this.deviceInfo = null;
        this.lastActive = null;
    }

    public ActiveSession(String username, String sessionId, String deviceInfo, LocalDateTime lastActive) {
        this.username = username;
        this.sessionId = sessionId;
        this.deviceInfo = deviceInfo;
        this.lastActive = lastActive;
    }
}
