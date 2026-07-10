package com.dvid.dcam.platform.logging;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.platform.database.AppDatabase;
import com.dvid.dcam.platform.database.dao.PendingLogDao;
import com.dvid.dcam.platform.database.entities.PendingLogEntity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class LogglyUploadWorker extends Worker {
    private static final int NORMAL_BATCH_SIZE = 80;
    private static final int RETRY_BATCH_SIZE = 20;
    private static final int MAX_ROUNDS_PER_RUN = 10;
    private static final int MAX_ATTEMPTS = 20;
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    private static final long BASE_RETRY_MS = 10_000L;
    private static final long MAX_RETRY_MS = 5L * 60 * 60 * 1_000;

    public LogglyUploadWorker(@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull @Override public Result doWork() {
        if (BuildConfig.LOGGLY_TOKEN.isBlank()) return Result.success();
        PendingLogDao dao;
        try {
            dao = AppDatabase.get(getApplicationContext()).pendingLogs();
        } catch (RuntimeException error) {
            LogglyDiagnostics.write(getApplicationContext(), "ERROR",
                    "Loggly upload could not open outbox database", error);
            return Result.retry();
        }
        String endpoint = "https://logs-01.loggly.com/inputs/" + BuildConfig.LOGGLY_TOKEN + "/tag/dcam/";
        int consecutiveFailures = 0;
        boolean globalBackoff = false;

        outer:
        for (int round = 0; round < MAX_ROUNDS_PER_RUN && !isStopped(); round++) {
            List<PendingLogEntity> normal = dao.oldestPending(NORMAL_BATCH_SIZE);
            List<PendingLogEntity> retries = dao.dueRetries(System.currentTimeMillis(), RETRY_BATCH_SIZE);
            if (normal.isEmpty() && retries.isEmpty()) break;

            for (PendingLogEntity event : normal) {
                if (isStopped()) return Result.retry();
                String failure = send(endpoint, payloadForSend(event));
                if (failure == null) {
                    dao.delete(event.id);
                    consecutiveFailures = 0;
                } else {
                    recordFailure(dao, event, failure);
                    if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                        globalBackoff = true;
                        break outer;
                    }
                }
            }

            for (PendingLogEntity event : retries) {
                if (isStopped()) return Result.retry();
                String failure = send(endpoint, payloadForSend(event));
                if (failure == null) {
                    dao.delete(event.id);
                    consecutiveFailures = 0;
                } else {
                    recordFailure(dao, event, failure);
                    if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                        globalBackoff = true;
                        break outer;
                    }
                }
            }
        }

        LogUploadScheduler.scheduleRetryWake(getApplicationContext(), dao.earliestRetryAt());
        if (globalBackoff || isStopped()) return Result.retry();
        if (dao.pendingCount() > 0) LogUploadScheduler.scheduleNow(getApplicationContext());
        return Result.success();
    }

    private void recordFailure(PendingLogDao dao, PendingLogEntity event, String error) {
        long now = System.currentTimeMillis();
        int attempts = event.attemptCount + 1;
        long firstFailedAt = event.firstFailedAt == 0 ? now : event.firstFailedAt;
        if (attempts >= MAX_ATTEMPTS) {
            dao.markDead(event.id, attempts, firstFailedAt, error);
            LogglyDiagnostics.write(getApplicationContext(), "ERROR",
                    "Log event quarantined after " + attempts + " attempts eventId=" + event.eventId, null);
        } else {
            dao.markRetry(event.id, attempts, now + retryDelayMillis(attempts), firstFailedAt, error);
        }
    }

    static long retryDelayMillis(int attemptCount) {
        long delay = BASE_RETRY_MS;
        for (int attempt = 1; attempt < attemptCount && delay < MAX_RETRY_MS; attempt++) {
            delay = Math.min(MAX_RETRY_MS, delay * 2);
        }
        return delay;
    }

    private String payloadForSend(PendingLogEntity event) {
        if (!event.payload.startsWith("{")) return event.payload;
        return "{\"sequenceId\":" + event.id + ",\"createdAtEpochMs\":" + event.createdAt + ","
                + event.payload.substring(1);
    }

    private String send(String endpoint, String payload) {
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
            LogglyDiagnostics.write(getApplicationContext(), "WARN", failure, null);
            return failure;
        } catch (Exception error) {
            String failure = "Loggly send failed: " + error.getMessage();
            LogglyDiagnostics.write(getApplicationContext(), "WARN", failure, null);
            return failure;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
