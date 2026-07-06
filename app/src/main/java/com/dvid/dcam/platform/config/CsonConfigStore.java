package com.dvid.dcam.platform.config;

import com.dvid.dcam.core.config.DcamConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class CsonConfigStore {
    private final File file;
    private final String defaultAccountUserId;

    public CsonConfigStore(File file) { this(file, DcamConfig.DEFAULT_ACCOUNT_USER_ID); }
    public CsonConfigStore(File file, String defaultAccountUserId) {
        this.file = file;
        this.defaultAccountUserId = defaultAccountUserId;
    }

    public DcamConfig load() throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            try (FileOutputStream output = new FileOutputStream(file)) {
                output.write(DefaultConfigs.text(defaultAccountUserId).getBytes(StandardCharsets.UTF_8));
            }
        }
        String text;
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            int offset = 0;
            while (offset < bytes.length) {
                int read = input.read(bytes, offset, bytes.length - offset);
                if (read < 0) break;
                offset += read;
            }
            text = new String(bytes, 0, offset, StandardCharsets.UTF_8);
        }
        String account = valueAfter(text, "account.user_id", defaultAccountUserId);
        if (DefaultConfigs.LEGACY_SAMPLE_ACCOUNT_USER_ID.equals(account)) {
            account = defaultAccountUserId;
            text = text.replace("account.user_id=\"" + DefaultConfigs.LEGACY_SAMPLE_ACCOUNT_USER_ID + "\"",
                    "account.user_id=\"" + escape(defaultAccountUserId) + "\"");
            try (FileOutputStream output = new FileOutputStream(file)) {
                output.write(text.getBytes(StandardCharsets.UTF_8));
            }
        }
        String police = valueAfter(text, "police.user_id", DcamConfig.DEFAULT_POLICE_USER_ID);
        boolean encrypted = "1".equals(valueAfter(text, "file.encryption", "0"));
        return new DcamConfig(account, police, encrypted);
    }

    private static String valueAfter(String text, String key, String fallback) {
        String prefix = key + "=";
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith(prefix)) {
                String value = trimmed.substring(prefix.length()).trim();
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\""))
                    value = value.substring(1, value.length() - 1);
                return value;
            }
        }
        return fallback;
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
