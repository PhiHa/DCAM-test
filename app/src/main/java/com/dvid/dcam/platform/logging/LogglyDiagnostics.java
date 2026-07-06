package com.dvid.dcam.platform.logging;

import android.content.Context;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class LogglyDiagnostics {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private LogglyDiagnostics() { }

    static synchronized void write(Context context, String level, String message, Throwable error) {
        File root = context.getExternalFilesDir(null);
        if (root == null) root = context.getFilesDir();
        File dir = new File(root, "log");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "loggly-internal.log");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.write(TIME.format(LocalDateTime.now()) + " " + level + " " + message);
            if (error != null) {
                writer.write(" - " + error.getClass().getName() + ": " + error.getMessage());
            }
            writer.newLine();
        } catch (Exception ignored) { }
    }
}
