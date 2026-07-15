package com.dvid.dcam.feature.auth.domain;

/** Shared input for hidden developer provisioning and a future cloud user adapter. */
public final class UserProvisioningRequest {
    private final String userId;
    private final String displayName;
    private final String passwordText;
    private final UserSource source;

    public UserProvisioningRequest(
            String userId,
            String displayName,
            String passwordText,
            UserSource source) {
        this.userId = userId;
        this.displayName = displayName;
        this.passwordText = passwordText;
        this.source = source;
    }

    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getPasswordText() { return passwordText; }
    public UserSource getSource() { return source; }
}
