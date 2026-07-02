package com.dvid.dcam.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DcamFileName {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmmss");
    private DcamFileName() {}

    public static String build(DcamFileType type, String accountUserId, String policeUserId,
                               LocalDateTime at, boolean encrypted) {
        String marker = type.getMarker().isEmpty() ? "" : "_" + type.getMarker();
        String enc = encrypted ? "_enc" : "";
        return "DSJ_" + accountUserId + "_" + policeUserId + "_" + DATE.format(at) + "_" +
                TIME.format(at) + marker + enc + "." + type.getExtension();
    }
}
