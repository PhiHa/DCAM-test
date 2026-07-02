package com.dvid.dcam.logging;

import android.content.Context;
import android.util.Log;
import com.dvid.dcam.BuildConfig;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DcamLogger {
    private static final String TAG = "DCAM";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ExecutorService LOGGLY_SENDER = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "loggly-sender");
        thread.setDaemon(true);
        return thread;
    });
    private static File logFile;
    private static String logglyUrl;
    private static String hardwareId = "unknown";
    private static String model = "unknown";
    private static String camId = "unknown";

    private DcamLogger() {}

    public static synchronized void init(Context context) {
        File root = context.getExternalFilesDir(null);
        if (root == null) root = context.getFilesDir();
        File dir = new File(root, "log");
        dir.mkdirs();
        logFile = new File(dir, "app.log");
        hardwareId = safe(android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID));
        model = safe((android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL).trim());
        if (!BuildConfig.LOGGLY_TOKEN.isBlank()) {
            logglyUrl = "https://logs-01.loggly.com/inputs/" + BuildConfig.LOGGLY_TOKEN + "/tag/dcam/";
        }
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
            try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
                out.println(line);
                if (error != null) error.printStackTrace(out);
            } catch (Exception ignored) {}
        }
        sendToLoggly(line, error);
    }

    private static void sendToLoggly(String line, Throwable error) {
        if (logglyUrl == null) return;
        String payload = json(line, error, Thread.currentThread().getName(), callerClass());
        LOGGLY_SENDER.execute(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(logglyUrl).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setDoOutput(true);
                byte[] body = payload.getBytes(StandardCharsets.UTF_8);
                connection.setFixedLengthStreamingMode(body.length);
                try (OutputStream out = connection.getOutputStream()) { out.write(body); }
                int code = connection.getResponseCode();
                if (code < 200 || code >= 300) writeInternal("Loggly send failed HTTP " + code + " log:" + payload);
            } catch (Exception sendError) {
                writeInternal("Loggly send failed: " + sendError.getMessage() + " log:" + payload);
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
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
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(TIME.format(LocalDateTime.now()) + " WARN " + message);
        } catch (Exception ignored) {}
    }

    private static int toAndroidLevel(String level) {
        if ("ERROR".equals(level)) return Log.ERROR;
        if ("WARN".equals(level)) return Log.WARN;
        return Log.INFO;
    }
}