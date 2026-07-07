package com.dvid.dcam.platform.logging;

import android.content.Context;
import android.util.Log;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.feature.device.domain.DeviceInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DcamLogger {
    private static final String TAG = "DCAM";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter LOG_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int LOCAL_LOG_RETENTION_DAYS = 14;
    private static File logDir;
    private static File logFile;
    private static LocalDate activeLogDate;
    private static LogOutbox logOutbox;
    private static String hardwareId = "unknown";
    private static String model = "unknown";
    private static String camId = "unknown";

    private DcamLogger() {}

    public static synchronized void init(Context context, DeviceInfo deviceInfo) {
        File root = context.getExternalFilesDir(null);
        if (root == null) root = context.getFilesDir();
        logDir = new File(root, "Logs");
        logDir.mkdirs();
        logFile = new File(logDir, "logs.txt");
        if (logOutbox == null) {
            try { logOutbox = new LogOutbox(context); }
            catch (Exception error) { writeInternal("Loggly outbox initialization failed: " + error.getMessage()); }
        }
        prepareLocalLog();
        hardwareId = safe(deviceInfo.getHardwareId());
        model = safe(deviceInfo.getModel());
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            e("Crash on " + thread.getName(), error);
            if (previous != null) previous.uncaughtException(thread, error);
        });
        i("Logger started: " + logFile.getAbsolutePath());
    }

    public static synchronized void setCamId(String nextCamId) { camId = safe(nextCamId); }

    public static void i(String message) { write("INFO", message, null); }
    public static void w(String message, Throwable error) { write("WARN", message, error); }
    public static void e(String message, Throwable error) { write("ERROR", message, error); }

    private static synchronized void write(String level, String message, Throwable error) {
        Log.println(toAndroidLevel(level), TAG, message + (error == null ? "" : "\n" + Log.getStackTraceString(error)));
        String thread = safe(Thread.currentThread().getName());
        String source = callerClass();
        String line = TIME.format(LocalDateTime.now()) + " " + level + " version=" + BuildConfig.VERSION_NAME
                + " thread=\"" + thread + "\" source=" + source + " hardwareId=" + hardwareId
                + " model=\"" + model + "\" camId=" + camId + " " + message;
        if (logFile != null) {
            prepareLocalLog();
            try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
                out.println(line);
                if (error != null) error.printStackTrace(out);
            } catch (Exception ignored) {}
        }
        String payload = json(line, error, Thread.currentThread().getName(), callerClass());
        if (logOutbox != null) logOutbox.enqueue(level, payload);
    }

    private static String json(String line, Throwable error, String thread, String source) {
        return "{\"app\":\"DCAM\",\"version\":\"" + escape(BuildConfig.VERSION_NAME) + "\",\"thread\":\""
                + escape(safe(thread)) + "\",\"source\":\"" + escape(source)
                + "\",\"hardwareId\":\"" + escape(hardwareId) + "\",\"model\":\""
                + escape(model) + "\",\"camId\":\"" + escape(camId) + "\",\"message\":\""
                + escape(line) + "\",\"stack\":\""
                + escape(error == null ? "" : Log.getStackTraceString(error)) + "\"}";
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String safe(String text) {
        return text == null || text.isBlank() ? "unknown" : text.trim().replaceAll("\\s+", " ");
    }

    private static String callerClass() {
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            String className = frame.getClassName();
            if (!className.equals(DcamLogger.class.getName()) && !className.equals(Thread.class.getName())) {
                int lastDot = className.lastIndexOf('.');
                return lastDot < 0 ? className : className.substring(lastDot + 1);
            }
        }
        return "unknown";
    }

    private static synchronized void writeInternal(String message) {
        if (logFile == null) return;
        prepareLocalLog();
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(TIME.format(LocalDateTime.now()) + " WARN " + message);
        } catch (Exception ignored) {}
    }

    private static int toAndroidLevel(String level) {
        if ("ERROR".equals(level)) return Log.ERROR;
        if ("WARN".equals(level)) return Log.WARN;
        return Log.INFO;
    }

    private static void prepareLocalLog() {
        if (logFile == null || logDir == null) return;
        LocalDate today = LocalDate.now();
        LocalDate rotatedDate = null;
        if (activeLogDate == null) activeLogDate = existingLogDate(today);
        if (!activeLogDate.equals(today) && logFile.exists() && logFile.length() > 0) {
            File archive = new File(logDir, "logs-" + LOG_DATE.format(activeLogDate) + ".txt");
            try {
                if (archive.exists()) appendFile(logFile, archive);
                else move(logFile, archive);
                rotatedDate = activeLogDate;
            } catch (Exception error) {
                Log.w(TAG, "Local log rotation failed", error);
            }
        }
        activeLogDate = today;
        deleteExpiredLocalLogs(today.minusDays(LOCAL_LOG_RETENTION_DAYS));
        if (rotatedDate != null && logOutbox != null) logOutbox.archiveDeadEvents(rotatedDate);
    }

    private static LocalDate existingLogDate(LocalDate fallback) {
        if (!logFile.exists() || logFile.length() == 0) return fallback;
        try {
            LocalDate modified = Instant.ofEpochMilli(logFile.lastModified())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            return modified.isAfter(fallback) ? fallback : modified;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static void deleteExpiredLocalLogs(LocalDate cutoff) {
        File[] files = logDir.listFiles((dir, name) -> name.startsWith("logs-") && name.endsWith(".txt"));
        if (files == null) return;
        for (File file : files) {
            String dateText = file.getName().substring(5, file.getName().length() - 4);
            try {
                if (LocalDate.parse(dateText, LOG_DATE).isBefore(cutoff) && !file.delete()) {
                    Log.w(TAG, "Could not delete expired local log " + file.getAbsolutePath());
                }
            } catch (DateTimeParseException ignored) { }
        }
    }

    private static void appendFile(File source, File target) throws Exception {
        try (FileInputStream input = new FileInputStream(source);
             FileOutputStream output = new FileOutputStream(target, true)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
        }
        if (!source.delete()) throw new IllegalStateException("Cannot delete rotated " + source);
    }

    private static void move(File source, File target) throws Exception {
        try {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
