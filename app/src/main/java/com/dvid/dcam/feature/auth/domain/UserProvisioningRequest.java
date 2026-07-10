package com.dvid.dcam.feature.auth.domain;

/** Shared input for hidden developer provisioning and a future cloud user adapter. */
public final class UserProvisioningRequest {
    private final String userId;
    private final String loginName;
    private final String displayName;
    private final String passwordText;
    private final UserSource source;

    public UserProvisioningRequest(
            String userId,
            String loginName,
            String displayName,
            String passwordText,
            UserSource source) {
        this.userId = userId;
        this.loginName = loginName;
        this.displayName = displayName;
        this.passwordText = passwordText;
        this.source = source;
    }

    public String getUserId() { return userId; }
    public String getLoginName() { return loginName; }
    public String getDisplayName() { return displayName; }
    public String getPasswordText() { return passwordText; }
    public UserSource getSource() { return source; }
}
