package com.dvid.dcam.platform.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.feature.settings.domain.StorageMode;
import com.dvid.dcam.platform.config.AndroidStorageModePreferenceStoreImpl;
import com.dvid.dcam.platform.config.AndroidVideoMd5PreferenceStoreImpl;
import com.dvid.dcam.platform.logging.DcamLogger;

/** Recovers staged media immediately after Android regains access to removable storage. */
public final class DcamStorageMountedReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) return;

        PendingResult pendingResult = goAsync();
        StorageMode mode = new AndroidStorageModePreferenceStoreImpl(
                context.getApplicationContext(), BuildConfig.STORAGE_MODE).currentStorageMode();
        DcamStorage storage = DcamStorage.from(context, mode);
        AndroidVideoMd5PreferenceStoreImpl videoMd5 = new AndroidVideoMd5PreferenceStoreImpl(
                context.getApplicationContext());
        new DcamMediaOutputImpl(storage,
                videoMd5::isVideoMd5Enabled)
                .recoverStaged(report -> {
            DcamLogger.i("Mounted-storage recovery: recovered=" + report.getRecovered()
                    + ", preserved=" + report.getPreserved()
                    + ", duplicates=" + report.getDuplicates());
            pendingResult.finish();
        });
    }
}
