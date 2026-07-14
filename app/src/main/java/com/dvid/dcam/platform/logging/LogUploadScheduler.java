package com.dvid.dcam.platform.logging;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

final class LogUploadScheduler {
    private static final int UPLOAD_JOB_ID = 0xDC04;

    private LogUploadScheduler() { }

    static void scheduleNow(Context context) {
        schedule(context, 0);
    }

    static void scheduleRetryWake(Context context, Long retryAt) {
        if (retryAt == null) return;
        schedule(context, Math.max(0, retryAt - System.currentTimeMillis()));
    }

    static void cancel(Context context) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler != null) scheduler.cancel(UPLOAD_JOB_ID);
    }

    private static void schedule(Context context, long delayMs) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) return;
        JobInfo job = new JobInfo.Builder(UPLOAD_JOB_ID,
                new ComponentName(context, LogglyUploadJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(delayMs)
                .setPersisted(true)
                .build();
        scheduler.schedule(job);
    }
}
