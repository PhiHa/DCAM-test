package com.dvid.dcam.platform.storage;

import java.util.Locale;

public enum DcamStorageMode {
    APP_DATA,
    PUBLIC_DCIM;

    public static DcamStorageMode from(String value) {
        if (value == null) return APP_DATA;
        String normalized = value.trim().replace('-', '_').toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) return APP_DATA;
        for (DcamStorageMode mode : values()) {
            if (mode.name().equals(normalized)) return mode;
        }
        return APP_DATA;
    }
}
