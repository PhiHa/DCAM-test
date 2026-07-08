package com.dvid.dcam.platform.config;

import com.dvid.dcam.core.config.domain.DcamConfig;
public final class DefaultConfigs {
    public static final String LEGACY_SAMPLE_ACCOUNT_USER_ID = "36NCC009910";

    private DefaultConfigs() {}

    public static String text(String accountUserId) {
        return text(accountUserId, DcamConfig.DEFAULT_MEDIA_ENCRYPTION_PASSWORD);
    }

    public static String text(String accountUserId, String mediaEncryptionPassword) {
        return "[device]\n" +
                "account.user_id=\"" + escape(accountUserId) + "\"\n" +
                "device.name=\"BodyCamera\"\n" +
                "serial_number=\"\"";
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
