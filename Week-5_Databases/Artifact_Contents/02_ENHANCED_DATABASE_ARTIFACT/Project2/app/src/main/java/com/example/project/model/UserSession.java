package com.example.project.model;

/** Identifies the account authenticated for the current dashboard session. */
public final class UserSession {
    private final long userId;
    private final String username;

    public UserSession(long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
