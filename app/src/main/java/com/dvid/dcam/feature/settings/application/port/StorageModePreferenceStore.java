package com.dvid.dcam.feature.settings.application.port;

import com.dvid.dcam.feature.settings.domain.StorageMode;

/** Persistence boundary for the operator-selected media storage policy. */
public interface StorageModePreferenceStore {
    StorageMode currentStorageMode();
    void selectStorageMode(StorageMode mode);
}
