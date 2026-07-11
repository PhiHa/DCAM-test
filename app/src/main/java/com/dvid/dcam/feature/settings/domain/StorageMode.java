package com.dvid.dcam.feature.settings.domain;

import java.util.Locale;

/** Operator-selected media storage policy. */
public enum StorageMode {
    INTERNAL,
    EXTERNAL,
    AUTO;

    public static StorageMode from(String value) {
        if (value == null || value.isBlank()) return AUTO;
        String normalized = value.trim().replace('-', '_').toUpperCase(Locale.ROOT);
        if ("APP_DATA".equals(normalized)) return INTERNAL;
        if ("PUBLIC_DCIM".equals(normalized)) return EXTERNAL;
        for (StorageMode mode : values()) {
            if (mode.name().equals(normalized)) return mode;
        }
        return AUTO;
    }
}
