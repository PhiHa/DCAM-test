package com.dvid.dcam.platform.logging;

import android.app.job.JobParameters;
import android.app.job.JobService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LogglyUploadJobService extends JobService {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean stopped;

    @Override public boolean onStartJob(JobParameters parameters) {
        stopped = false;
        executor.execute(() -> {
            Long retryAt;
            try {
                retryAt = LogglyOutboxSender.sendPending(getApplicationContext(), () -> stopped);
            } catch (RuntimeException error) {
                LogglyDiagnostics.write(getApplicationContext(), "ERROR", "Loggly job failed", error);
                retryAt = System.currentTimeMillis() + 10_000L;
            }
            jobFinished(parameters, false);
            LogUploadScheduler.scheduleRetryWake(getApplicationContext(), retryAt);
        });
        return true;
    }

    @Override public boolean onStopJob(JobParameters parameters) {
        stopped = true;
        return true;
    }

    @Override public void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
