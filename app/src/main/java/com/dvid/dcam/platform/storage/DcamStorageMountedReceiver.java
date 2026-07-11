package com.dvid.dcam.platform.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dvid.dcam.BuildConfig;
import com.dvid.dcam.feature.settings.domain.StorageMode;
import com.dvid.dcam.platform.config.AndroidStorageModePreferenceStoreImpl;

/** Recovers staged media immediately after Android regains access to removable storage. */
public final class DcamStorageMountedReceiver extends BroadcastReceiver {
    private static final String TAG = "DcamStorageRecovery";

    @Override public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) return;

        PendingResult pendingResult = goAsync();
        StorageMode mode = new AndroidStorageModePreferenceStoreImpl(
                context.getApplicationContext(), BuildConfig.STORAGE_MODE).currentStorageMode();
        DcamStorage storage = DcamStorage.from(context, mode);
        new DcamMediaOutputImpl(storage).recoverStaged(report -> {
            Log.i(TAG, "Mounted-storage recovery: recovered=" + report.getRecovered()
                    + ", preserved=" + report.getPreserved()
                    + ", duplicates=" + report.getDuplicates());
            pendingResult.finish();
        });
    }
}
