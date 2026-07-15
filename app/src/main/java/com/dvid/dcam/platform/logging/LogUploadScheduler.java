package com.dvid.dcam.platform.logging;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

final class LogUploadScheduler {
    private static final int UPLOAD_JOB_ID = 0xDC04;
    private static final int RETRY_JOB_ID = 0xDC05;
    private static volatile boolean continuousDrainEnabled;

    private LogUploadScheduler() { }

    static void scheduleNow(Context context) {
        if (continuousDrainEnabled) return;
        schedule(context, UPLOAD_JOB_ID, 0);
    }

    static void scheduleRetryWake(Context context, Long retryAt) {
        if (continuousDrainEnabled) return;
        if (retryAt == null) return;
        schedule(context, RETRY_JOB_ID, Math.max(0, retryAt - System.currentTimeMillis()));
    }

    static void cancel(Context context) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) return;
        scheduler.cancel(UPLOAD_JOB_ID);
        scheduler.cancel(RETRY_JOB_ID);
    }

    static boolean isRetryJob(int jobId) {
        return jobId == RETRY_JOB_ID;
    }

    static void setContinuousDrainEnabled(boolean enabled) {
        continuousDrainEnabled = enabled;
    }

    private static void schedule(Context context, int jobId, long delayMs) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) return;
        JobInfo job = new JobInfo.Builder(jobId,
                new ComponentName(context, LogglyUploadJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(delayMs)
                .setPersisted(true)
                .build();
        scheduler.schedule(job);
    }
}
