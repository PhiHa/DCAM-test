package com.dvid.dcam.platform.logging;

import android.content.Context;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

final class LogUploadScheduler {
    private static final String SEND_WORK = "dcam-loggly-send";
    private static final String RETRY_WAKE_WORK = "dcam-loggly-retry-wake";

    private LogUploadScheduler() { }

    static void scheduleNow(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LogglyUploadWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .addTag(SEND_WORK)
                .build();
        WorkManager.getInstance(context.getApplicationContext()).enqueueUniqueWork(
                SEND_WORK, ExistingWorkPolicy.APPEND_OR_REPLACE, request);
    }

    static void scheduleRetryWake(Context context, Long retryAt) {
        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        if (retryAt == null) {
            workManager.cancelUniqueWork(RETRY_WAKE_WORK);
            return;
        }
        long delay = Math.max(0, retryAt - System.currentTimeMillis());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LogRetryWakeWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(RETRY_WAKE_WORK)
                .build();
        workManager.enqueueUniqueWork(RETRY_WAKE_WORK, ExistingWorkPolicy.REPLACE, request);
    }

    static void cancel(Context context) {
        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        workManager.cancelUniqueWork(SEND_WORK);
        workManager.cancelUniqueWork(RETRY_WAKE_WORK);
    }
}
