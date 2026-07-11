package com.dvid.dcam.platform.config;

import android.content.Context;
import android.content.SharedPreferences;
import com.dvid.dcam.feature.settings.application.port.StorageModePreferenceStore;
import com.dvid.dcam.feature.settings.domain.StorageMode;

/** Android persistence for the media storage selection. */
public final class AndroidStorageModePreferenceStoreImpl implements StorageModePreferenceStore {
    private static final String PREFS_NAME = "dcam_storage";
    private static final String KEY_MODE = "mode";

    private final Context context;
    private final StorageMode defaultMode;

    public AndroidStorageModePreferenceStoreImpl(Context context, String defaultMode) {
        this.context = context.getApplicationContext();
        this.defaultMode = StorageMode.from(defaultMode);
    }

    @Override public StorageMode currentStorageMode() {
        return StorageMode.from(prefs().getString(KEY_MODE, defaultMode.name()));
    }

    @Override public void selectStorageMode(StorageMode mode) {
        prefs().edit().putString(KEY_MODE, mode.name()).apply();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
