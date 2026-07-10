package com.dvid.dcam.feature.settings.application.usecase;

/** Application entry point for media encryption settings. */
public interface MediaEncryptionSettingsUseCase {
    boolean isMediaEncryptionEnabled();
    void setMediaEncryptionEnabled(boolean enabled);
}
