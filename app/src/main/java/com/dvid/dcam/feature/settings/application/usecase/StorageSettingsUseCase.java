package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.domain.StorageMode;

/** Application entry point for media storage selection. */
public interface StorageSettingsUseCase {
    StorageMode currentMode();
    StorageMode[] supportedModes();
    void changeMode(StorageMode mode);
}
