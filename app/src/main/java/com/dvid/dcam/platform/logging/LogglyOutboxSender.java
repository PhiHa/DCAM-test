package com.dvid.dcam.platform.logging;

import android.content.Context;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.platform.database.AppDatabase;
import com.dvid.dcam.platform.database.dao.PendingLogDao;
import com.dvid.dcam.platform.database.entities.PendingLogEntity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BooleanSupplier;

final class LogglyOutboxSender {
    private static final int NORMAL_BATCH_SIZE = 80;
    private static final int RETRY_BATCH_SIZE = 20;
    private static final int MAX_ATTEMPTS = 20;
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    private static final long BASE_RETRY_MS = 10_000L;
    private static final long MAX_RETRY_MS = 5L * 60 * 60 * 1_000;

    private LogglyOutboxSender() { }

    static Long sendPending(Context context, BooleanSupplier stopped) {
        if (BuildConfig.LOGGLY_TOKEN.isBlank()) return null;
        PendingLogDao dao;
        try {
            dao = AppDatabase.get(context).pendingLogs();
        } catch (RuntimeException error) {
            LogglyDiagnostics.write(context, "ERROR", "Loggly upload could not open outbox database", error);
            return System.currentTimeMillis() + BASE_RETRY_MS;
        }
        String endpoint = "https://logs-01.loggly.com/inputs/" + BuildConfig.LOGGLY_TOKEN + "/tag/dcam/";
        int consecutiveFailures = 0;

        outer:
        while (!stopped.getAsBoolean()) {
            List<PendingLogEntity> normal = dao.oldestPending(NORMAL_BATCH_SIZE);
            List<PendingLogEntity> retries = dao.dueRetries(System.currentTimeMillis(), RETRY_BATCH_SIZE);
            if (normal.isEmpty() && retries.isEmpty()) break;
            for (PendingLogEntity event : normal) {
                if (stopped.getAsBoolean()) return System.currentTimeMillis() + BASE_RETRY_MS;
                String failure = send(context, endpoint, payloadForSend(event));
                if (failure == null) {
                    dao.delete(event.id);
                    consecutiveFailures = 0;
                } else {
                    recordFailure(context, dao, event, failure);
                    if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) break outer;
                }
            }
            for (PendingLogEntity event : retries) {
                if (stopped.getAsBoolean()) return System.currentTimeMillis() + BASE_RETRY_MS;
                String failure = send(context, endpoint, payloadForSend(event));
                if (failure == null) {
                    dao.delete(event.id);
                    consecutiveFailures = 0;
                } else {
                    recordFailure(context, dao, event, failure);
                    if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) break outer;
                }
            }
        }
        if (stopped.getAsBoolean()) return System.currentTimeMillis() + BASE_RETRY_MS;
        return dao.earliestRetryAt();
    }

    static boolean hasDeliverableEvents(Context context) {
        return AppDatabase.get(context).pendingLogs().deliverableCount() > 0;
    }

    static long retryDelayMillis(int attemptCount) {
        long delay = BASE_RETRY_MS;
        for (int attempt = 1; attempt < attemptCount && delay < MAX_RETRY_MS; attempt++) {
            delay = Math.min(MAX_RETRY_MS, delay * 2);
        }
        return delay;
    }

    private static void recordFailure(
            Context context, PendingLogDao dao, PendingLogEntity event, String error) {
        long now = System.currentTimeMillis();
        int attempts = event.attemptCount + 1;
        long firstFailedAt = event.firstFailedAt == 0 ? now : event.firstFailedAt;
        if (attempts >= MAX_ATTEMPTS) {
            dao.markDead(event.id, attempts, firstFailedAt, error);
            LogglyDiagnostics.write(context, "ERROR",
                    "Log event quarantined after " + attempts + " attempts eventId=" + event.eventId, null);
        } else {
            dao.markRetry(event.id, attempts, now + retryDelayMillis(attempts), firstFailedAt, error);
        }
    }

    private static String payloadForSend(PendingLogEntity event) {
        if (!event.payload.startsWith("{")) return event.payload;
        return "{\"sequenceId\":" + event.id + ",\"createdAtEpochMs\":" + event.createdAt + ","
                + event.payload.substring(1);
    }

    private static String send(Context context, String endpoint, String payload) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            byte[] body = payload.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(body.length);
            try (OutputStream output = connection.getOutputStream()) { output.write(body); }
            int code = connection.getResponseCode();
            if (code >= 200 && code < 300) return null;
            String failure = "Loggly send failed HTTP " + code;
            LogglyDiagnostics.write(context, "WARN", failure, null);
            return failure;
        } catch (Exception error) {
            String failure = "Loggly send failed: " + error.getMessage();
            LogglyDiagnostics.write(context, "WARN", failure, null);
            return failure;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
