package com.dvid.dcam.feature.auth.domain;

public final class UserProvisioningResult {
    private final boolean successful;
    private final String errorCode;

    private UserProvisioningResult(boolean successful, String errorCode) {
        this.successful = successful;
        this.errorCode = errorCode;
    }

    public static UserProvisioningResult success() {
        return new UserProvisioningResult(true, null);
    }

    public static UserProvisioningResult failure(String errorCode) {
        return new UserProvisioningResult(false, errorCode);
    }

    public boolean isSuccessful() { return successful; }
    public String getErrorCode() { return errorCode; }
}
