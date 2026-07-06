package com.dvid.dcam.platform.logging;

import android.content.Context;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.platform.database.AppDatabase;
import com.dvid.dcam.platform.database.dao.PendingLogDao;
import com.dvid.dcam.platform.database.entities.PendingLogEntity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final class LogOutbox {
    private static final long RETENTION_MS = 14L * 24 * 60 * 60 * 1_000;
    private static final long SOFT_LIMIT_BYTES = 100L * 1024 * 1024;
    private static final long HARD_LIMIT_BYTES = 150L * 1024 * 1024;
    private static final int PRUNE_CHUNK = 100;
    private static final int SUMMARY_EVENT_ID_LIMIT = 50;

    private final Context context;
    private final AppDatabase database;
    private final PendingLogDao dao;
    private final ExecutorService writer = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "log-outbox-writer");
        thread.setDaemon(true);
        return thread;
    });

    LogOutbox(Context context) {
        this.context = context.getApplicationContext();
        database = AppDatabase.get(this.context);
        dao = database.pendingLogs();
        writer.execute(() -> {
            prune();
            if (!BuildConfig.LOGGLY_TOKEN.isBlank()) {
                if (dao.pendingCount() > 0) LogUploadScheduler.scheduleNow(this.context);
                LogUploadScheduler.scheduleRetryWake(this.context, dao.earliestRetryAt());
            }
        });
    }

    void enqueue(String level, String payload) {
        runAndWait(() -> {
            dao.insert(newEvent(level, payload));
            prune();
            if (!BuildConfig.LOGGLY_TOKEN.isBlank() && dao.pendingCount() == 1) {
                LogUploadScheduler.scheduleNow(context);
            }
        }, "Failed to persist Loggly event");
    }

    void archiveDeadEvents(LocalDate archiveDate) {
        runAndWait(() -> archiveDeadEventsInternal(archiveDate), "Failed to archive dead Loggly events");
    }

    private PendingLogEntity newEvent(String level, String payload) {
        String eventId = UUID.randomUUID().toString();
        String identifiedPayload = payload.startsWith("{")
                ? "{\"eventId\":\"" + eventId + "\"," + payload.substring(1)
                : payload;
        return new PendingLogEntity(eventId, System.currentTimeMillis(), level, identifiedPayload);
    }

    private void archiveDeadEventsInternal(LocalDate archiveDate) {
        List<PendingLogEntity> dead = dao.deadEvents();
        File logDir = logDir();
        deleteExpiredDeadLogs(logDir, LocalDate.now().minusDays(14));
        if (dead.isEmpty()) return;

        File archive = new File(logDir, "loggly-dead-" + archiveDate + ".log");
        try (FileOutputStream stream = new FileOutputStream(archive, true);
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) {
            for (PendingLogEntity event : dead) {
                output.write(deadArchiveLine(event));
                output.newLine();
            }
            output.flush();
            stream.getFD().sync();
        } catch (Exception error) {
            LogglyDiagnostics.write(context, "ERROR", "Dead-event archive write failed", error);
            return;
        }

        List<Long> deadIds = new ArrayList<>();
        for (PendingLogEntity event : dead) {
            deadIds.add(event.id);
        }
        PendingLogEntity summary = newEvent("WARN", summaryPayload(archive.getName(), dead));
        database.runInTransaction(() -> {
            dao.insert(summary);
            dao.deleteIds(deadIds);
        });
        if (!BuildConfig.LOGGLY_TOKEN.isBlank()) LogUploadScheduler.scheduleNow(context);
    }

    private String summaryPayload(String archiveName, List<PendingLogEntity> events) {
        long first = Long.MAX_VALUE;
        long last = Long.MIN_VALUE;
        int info = 0, warn = 0, error = 0;
        int sampled = 0;
        StringBuilder ids = new StringBuilder();
        for (PendingLogEntity event : events) {
            first = Math.min(first, event.createdAt);
            last = Math.max(last, event.createdAt);
            if ("ERROR".equals(event.level)) error++;
            else if ("WARN".equals(event.level)) warn++;
            else info++;
            if (sampled >= SUMMARY_EVENT_ID_LIMIT) continue;
            if (ids.length() > 0) ids.append(',');
            ids.append('"').append(escape(event.eventId)).append('"');
            sampled++;
        }
        return "{\"type\":\"loggly_dead_summary\",\"archive\":\"" + escape(archiveName)
                + "\",\"deadCount\":" + events.size() + ",\"firstEventAt\":" + first
                + ",\"lastEventAt\":" + last + ",\"levels\":{\"INFO\":" + info
                + ",\"WARN\":" + warn + ",\"ERROR\":" + error + "},\"eventIdSample\":[" + ids + "]}";
    }

    private String deadArchiveLine(PendingLogEntity event) {
        return "{\"eventId\":\"" + escape(event.eventId) + "\",\"attemptCount\":" + event.attemptCount
                + ",\"firstFailedAt\":" + event.firstFailedAt + ",\"lastError\":\""
                + escape(event.lastError == null ? "" : event.lastError) + "\",\"payload\":" + event.payload + "}";
    }

    private File logDir() {
        File root = context.getExternalFilesDir(null);
        if (root == null) root = context.getFilesDir();
        File dir = new File(root, "log");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private void deleteExpiredDeadLogs(File dir, LocalDate cutoff) {
        File[] files = dir.listFiles((parent, name) -> name.startsWith("loggly-dead-") && name.endsWith(".log"));
        if (files == null) return;
        for (File file : files) {
            String date = file.getName().substring("loggly-dead-".length(), file.getName().length() - 4);
            try {
                if (LocalDate.parse(date).isBefore(cutoff) && !file.delete()) {
                    LogglyDiagnostics.write(context, "WARN", "Could not delete expired " + file.getName(), null);
                }
            } catch (DateTimeParseException ignored) { }
        }
    }

    private void runAndWait(Runnable action, String failureMessage) {
        try {
            Future<?> saved = writer.submit(action);
            saved.get();
        } catch (Exception error) {
            LogglyDiagnostics.write(context, "ERROR", failureMessage, error);
            if (error instanceof InterruptedException) Thread.currentThread().interrupt();
        }
    }

    private void prune() {
        dao.deleteExpiredLowPriority(System.currentTimeMillis() - RETENTION_MS);
        while (dao.payloadBytes() > SOFT_LIMIT_BYTES && dao.deleteOldestLowPriority(PRUNE_CHUNK) > 0) { }
        boolean hardPruned = false;
        while (dao.payloadBytes() > HARD_LIMIT_BYTES && dao.deleteOldest(PRUNE_CHUNK) > 0) hardPruned = true;
        if (hardPruned) {
            LogglyDiagnostics.write(context, "ERROR", "Log outbox exceeded hard limit; oldest events removed", null);
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
