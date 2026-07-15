package com.dvid.dcam.core.config.domain;

/** Immutable application configuration consumed by domain/application workflows. */
public final class DcamConfig {
    public static final String DEFAULT_ACCOUNT_USER_ID = "000000";
    public static final String DEFAULT_POLICE_USER_ID = "B01OPR";
    public static final boolean DEFAULT_VIDEO_ENCRYPTED = false;
    public static final String DEFAULT_MEDIA_ENCRYPTION_PASSWORD = "123456";

    private final String accountUserId;
    private final String policeUserId;
    private final boolean videoEncrypted;
    private final String mediaEncryptionPassword;

    public DcamConfig() {
        this(DEFAULT_ACCOUNT_USER_ID, DEFAULT_POLICE_USER_ID,
                DEFAULT_VIDEO_ENCRYPTED, DEFAULT_MEDIA_ENCRYPTION_PASSWORD);
    }

    public static DcamConfig defaults(String accountUserId) {
        return new DcamConfig(accountUserId, DEFAULT_POLICE_USER_ID,
                DEFAULT_VIDEO_ENCRYPTED, DEFAULT_MEDIA_ENCRYPTION_PASSWORD);
    }

    public DcamConfig(String accountUserId, String policeUserId, boolean videoEncrypted) {
        this(accountUserId, policeUserId, videoEncrypted, DEFAULT_MEDIA_ENCRYPTION_PASSWORD);
    }

    public DcamConfig(String accountUserId, String policeUserId, boolean videoEncrypted,
                      String mediaEncryptionPassword) {
        this.accountUserId = accountUserId;
        this.policeUserId = policeUserId;
        this.videoEncrypted = videoEncrypted;
        this.mediaEncryptionPassword = mediaEncryptionPassword == null || mediaEncryptionPassword.trim().isEmpty()
                ? DEFAULT_MEDIA_ENCRYPTION_PASSWORD : mediaEncryptionPassword;
    }

    public String getAccountUserId() { return accountUserId; }
    public String getPoliceUserId() { return policeUserId; }
    public boolean isVideoEncrypted() { return videoEncrypted; }
    public String getMediaEncryptionPassword() { return mediaEncryptionPassword; }
}
