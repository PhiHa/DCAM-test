package com.dvid.dcam.config;

public final class DefaultConfigs {
    private DefaultConfigs() {}

    public static final String TEXT =
            "[video]\n" +
            "file.encryption=\"1\"\n" +
            "file.duration=\"30\"\n" +
            "main.resolution=\"1920_1080\"\n" +
            "main.frame_rate=\"25\"\n" +
            "main.encoder=\"video/hevc\"\n" +
            "[device]\n" +
            "account.user_id=\"36NCC009910\"\n" +
            "police.user_id=\"000000\"\n" +
            "device.name=\"BodyCamera\"\n" +
            "[common]\n" +
            "date.ymd_0format=\"yyyyMMdd\"\n" +
            "date.hms_0format=\"HHmmss\"\n" +
            "file.name_format_map=[\"DSJ\",\"%DEVICE_ID%\",\"%POLICE_ID%\",\"%DATE_YMD%\",\"%DATE_HMS%\",\"%MARK%\"]";
}
