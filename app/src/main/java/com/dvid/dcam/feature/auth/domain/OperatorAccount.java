package com.dvid.dcam.feature.auth.domain;

import java.util.Objects;

/** Stable operator identity. {@code fileUserId} is the six-digit evidence-file identity. */
public final class OperatorAccount {
    private final String userId;
    private final String fileUserId;
    private final String displayName;
    private final UserSource source;
    private final boolean active;

    public OperatorAccount(
            String userId,
            String fileUserId,
            String displayName,
            UserSource source,
            boolean active) {
        this.userId = Objects.requireNonNull(userId);
        this.fileUserId = Objects.requireNonNull(fileUserId);
        this.displayName = Objects.requireNonNull(displayName);
        this.source = Objects.requireNonNull(source);
        this.active = active;
    }

    public String getUserId() { return userId; }
    public String getFileUserId() { return fileUserId; }
    public String getDisplayName() { return displayName; }
    public UserSource getSource() { return source; }
    public boolean isActive() { return active; }
}
