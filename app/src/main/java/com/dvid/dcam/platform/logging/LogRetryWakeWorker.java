package com.dvid.dcam.platform.logging;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public final class LogRetryWakeWorker extends Worker {
    public LogRetryWakeWorker(@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull @Override public Result doWork() {
        LogUploadScheduler.scheduleNow(getApplicationContext());
        return Result.success();
    }
}
