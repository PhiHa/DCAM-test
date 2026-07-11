package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.application.port.StorageModePreferenceStore;
import com.dvid.dcam.feature.settings.domain.StorageMode;

public final class StorageSettingsUseCaseImpl implements StorageSettingsUseCase {
    private final StorageModePreferenceStore preferences;

    public StorageSettingsUseCaseImpl(StorageModePreferenceStore preferences) {
        this.preferences = preferences;
    }

    @Override public StorageMode currentMode() { return preferences.currentStorageMode(); }

    @Override public StorageMode[] supportedModes() { return StorageMode.values(); }

    @Override public void changeMode(StorageMode mode) {
        preferences.selectStorageMode(mode == null ? StorageMode.AUTO : mode);
    }
}
