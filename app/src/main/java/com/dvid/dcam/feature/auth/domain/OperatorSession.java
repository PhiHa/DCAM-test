package com.dvid.dcam.feature.auth.domain;

import java.util.Objects;

/** Immutable identity snapshot used by the app and evidence naming during one login session. */
public final class OperatorSession {
    private final String sessionId;
    private final String userId;
    private final String fileUserId;
    private final String displayName;
    private final String bootId;
    private final long startedAtEpochMillis;

    public OperatorSession(
            String sessionId,
            String userId,
            String fileUserId,
            String displayName,
            String bootId,
            long startedAtEpochMillis) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.userId = Objects.requireNonNull(userId);
        this.fileUserId = Objects.requireNonNull(fileUserId);
        this.displayName = Objects.requireNonNull(displayName);
        this.bootId = Objects.requireNonNull(bootId);
        this.startedAtEpochMillis = startedAtEpochMillis;
    }

    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getFileUserId() { return fileUserId; }
    public String getDisplayName() { return displayName; }
    public String getBootId() { return bootId; }
    public long getStartedAtEpochMillis() { return startedAtEpochMillis; }
}
