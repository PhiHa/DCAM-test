package com.dvid.dcam.platform.config;

import com.dvid.dcam.core.config.domain.DcamConfig;
public final class DefaultConfigs {
    public static final String LEGACY_SAMPLE_ACCOUNT_USER_ID = "36NCC009910";

    private DefaultConfigs() {}

    public static String text(String accountUserId) {
        return "[video]\n" +
                "file.encryption=\"1\"\n" +
                "file.duration=\"30\"\n" +
                "main.resolution=\"1920_1080\"\n" +
                "main.frame_rate=\"25\"\n" +
                "main.encoder=\"video/hevc\"\n" +
                "[device]\n" +
                "account.user_id=\"" + escape(accountUserId) + "\"\n" +
                "police.user_id=\"" + DcamConfig.DEFAULT_POLICE_USER_ID + "\"\n" +
                "device.name=\"BodyCamera\"\n" +
                "[common]\n" +
                "date.ymd_0format=\"yyyyMMdd\"\n" +
                "date.hms_0format=\"HHmmss\"\n" +
                "file.name_format_map=[\"DSJ\",\"%DEVICE_ID%\",\"%POLICE_ID%\",\"%DATE_YMD%\",\"%DATE_HMS%\",\"%MARK%\"]";
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
