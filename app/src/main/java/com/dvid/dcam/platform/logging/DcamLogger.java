package com.dvid.dcam.platform.logging;

import android.content.Context;
import android.util.Log;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.feature.device.domain.DeviceInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DcamLogger {
    private static final String TAG = "DCAM";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter LOG_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final ZoneId BDMA_TIME_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final int LOCAL_LOG_RETENTION_DAYS = 14;
    private static final long CRASH_FALLBACK_JOIN_MS = 2_500L;
    private static Context appContext;
    private static File logDir;
    private static File logFile;
    private static LocalDate activeLogDate;
    private static LogOutbox logOutbox;
    private static String hardwareId = "unknown";
    private static String model = "unknown";
    private static String camId = "unknown";
    private static boolean remoteUploadsEnabled;
    private static boolean crashHandlerInstalled;

    private DcamLogger() {
    }

    public static synchronized void bootstrap(Context context) {
        appContext = context.getApplicationContext();
        if (logFile == null) {
            File root = appContext.getExternalFilesDir(null);
            if (root == null) root = appContext.getFilesDir();
            logDir = new File(root, "Logs");
            logDir.mkdirs();
            logFile = new File(logDir, "logs.txt");
            prepareLocalLog();
        }
        installCrashHandler();
        if (logOutbox == null) {
            try {
                logOutbox = new LogOutbox(appContext, remoteUploadsEnabled);
            } catch (Exception error) {
                writeInternal("Loggly outbox initialization failed: " + error.getMessage());
                sendDirect("Loggly outbox initialization failed", error);
            }
        }
    }

    public static synchronized void init(Context context, DeviceInfo deviceInfo) {
        bootstrap(context);
        hardwareId = safe(deviceInfo.getHardwareId());
        model = safe(deviceInfo.getModel());
        i("Logger started: " + logFile.getAbsolutePath());
    }

    private static void installCrashHandler() {
        if (crashHandlerInstalled) return;
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            e("Crash on " + thread.getName(), error);
            if (previous != null) previous.uncaughtException(thread, error);
        });
        crashHandlerInstalled = true;
    }

    public static synchronized void setCamId(String nextCamId) {
        camId = safe(nextCamId);
    }

    public static synchronized void setRemoteUploadsEnabled(boolean enabled) {
        remoteUploadsEnabled = enabled;
        if (logOutbox != null)
            logOutbox.setUploadsEnabled(enabled);
    }

    public static void i(String message) {
        write("INFO", message, null);
    }

    public static void w(String message, Throwable error) {
        write("WARN", message, error);
    }

    public static void e(String message, Throwable error) {
        write("ERROR", message, error);
    }

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
                if (error != null)
                    error.printStackTrace(out);
            } catch (Exception ignored) {
            }
        }
        String payload = json(message, error, Thread.currentThread().getName(), callerClass());
        boolean enqueued = remoteUploadsEnabled && logOutbox != null
                && logOutbox.enqueue(level, payload);
        if (shouldSendCrashFallback(level, message, remoteUploadsEnabled, enqueued))
            sendCrashFallback(payload);
    }

    static boolean shouldSendCrashFallback(
            String level, String message, boolean uploadsEnabled, boolean enqueued) {
        return uploadsEnabled && !enqueued && "ERROR".equals(level)
                && message != null && message.startsWith("Crash on ");
    }

    private static void sendCrashFallback(String payload) {
        if (BuildConfig.LOGGLY_TOKEN.isBlank())
            return;
        Thread sender = new Thread(() -> sendCrashFallbackOnWorker(payload), "loggly-crash-fallback");
        sender.start();
        try {
            sender.join(CRASH_FALLBACK_JOIN_MS);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }

    public static void setContinuousDrainEnabled(boolean enabled) {
        LogUploadScheduler.setContinuousDrainEnabled(enabled);
    }

    static void sendDirect(String message, Throwable error) {
        sendCrashFallback(json(message, error, Thread.currentThread().getName(),
                DcamLogger.class.getName()));
    }

    public static void sendBootstrapFailure(String message, Throwable error) {
        sendDirect(message, error);
    }

    private static void sendCrashFallbackOnWorker(String payload) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(
                    "https://logs-01.loggly.com/inputs/" + BuildConfig.LOGGLY_TOKEN + "/tag/dcam/")
                    .openConnection();
            connection.setConnectTimeout(1_500);
            connection.setReadTimeout(1_500);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            byte[] body = payload.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(body.length);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300)
                Log.w(TAG, "Loggly crash fallback failed HTTP " + code);
        } catch (Exception error) {
            Log.w(TAG, "Loggly crash fallback failed", error);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    static String json(String message, Throwable error, String thread, String source) {
        String timestamp = OffsetDateTime.now(BDMA_TIME_ZONE)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return "{\"app\":\"DCAM\",\"version\":\"" + escape(BuildConfig.VERSION_NAME) + "\",\"timestamp\":\""
                + escape(timestamp) + "\",\"thread\":\"" + escape(safe(thread)) + "\",\"source\":\""
                + escape(source) + "\",\"hardwareId\":\"" + escape(hardwareId) + "\",\"model\":\""
                + escape(model) + "\",\"camId\":\"" + escape(camId) + "\",\"message\":\""
                + escape(message == null ? "" : message) + "\",\"stack\":\""
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
        if (logFile == null)
            return;
        prepareLocalLog();
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(TIME.format(LocalDateTime.now()) + " WARN " + message);
        } catch (Exception ignored) {
        }
    }

    private static int toAndroidLevel(String level) {
        if ("ERROR".equals(level))
            return Log.ERROR;
        if ("WARN".equals(level))
            return Log.WARN;
        return Log.INFO;
    }

    private static void prepareLocalLog() {
        if (logFile == null || logDir == null)
            return;
        LocalDate today = LocalDate.now();
        LocalDate rotatedDate = null;
        if (activeLogDate == null)
            activeLogDate = existingLogDate(today);
        if (!activeLogDate.equals(today) && logFile.exists() && logFile.length() > 0) {
            File archive = new File(logDir, "logs-" + LOG_DATE.format(activeLogDate) + ".txt");
            try {
                if (archive.exists())
                    appendFile(logFile, archive);
                else
                    move(logFile, archive);
                rotatedDate = activeLogDate;
            } catch (Exception error) {
                Log.w(TAG, "Local log rotation failed", error);
            }
        }
        activeLogDate = today;
        deleteExpiredLocalLogs(today.minusDays(LOCAL_LOG_RETENTION_DAYS));
        if (rotatedDate != null && logOutbox != null)
            logOutbox.archiveDeadEvents(rotatedDate);
    }

    private static LocalDate existingLogDate(LocalDate fallback) {
        if (!logFile.exists() || logFile.length() == 0)
            return fallback;
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
        if (files == null)
            return;
        for (File file : files) {
            String dateText = file.getName().substring(5, file.getName().length() - 4);
            try {
                if (LocalDate.parse(dateText, LOG_DATE).isBefore(cutoff) && !file.delete()) {
                    Log.w(TAG, "Could not delete expired local log " + file.getAbsolutePath());
                }
            } catch (DateTimeParseException ignored) {
            }
        }
    }

    private static void appendFile(File source, File target) throws Exception {
        try (FileInputStream input = new FileInputStream(source);
                FileOutputStream output = new FileOutputStream(target, true)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1)
                output.write(buffer, 0, read);
        }
        if (!source.delete())
            throw new IllegalStateException("Cannot delete rotated " + source);
    }

    private static void move(File source, File target) throws Exception {
        try {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
