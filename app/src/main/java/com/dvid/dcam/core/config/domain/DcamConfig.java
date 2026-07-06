package com.dvid.dcam.core.config.domain;

/** Immutable application configuration consumed by domain/application workflows. */
public final class DcamConfig {
    public static final String DEFAULT_ACCOUNT_USER_ID = "000000";
    public static final String DEFAULT_POLICE_USER_ID = "000000";

    private final String accountUserId;
    private final String policeUserId;
    private final boolean videoEncrypted;

    public DcamConfig() {
        this(DEFAULT_ACCOUNT_USER_ID, DEFAULT_POLICE_USER_ID, true);
    }

    public static DcamConfig defaults(String accountUserId) {
        return new DcamConfig(accountUserId, DEFAULT_POLICE_USER_ID, true);
    }

    public DcamConfig(String accountUserId, String policeUserId, boolean videoEncrypted) {
        this.accountUserId = accountUserId;
        this.policeUserId = policeUserId;
        this.videoEncrypted = videoEncrypted;
    }

    public String getAccountUserId() { return accountUserId; }
    public String getPoliceUserId() { return policeUserId; }
    public boolean isVideoEncrypted() { return videoEncrypted; }
}
