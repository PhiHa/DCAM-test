package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.application.port.MediaEncryptionPreferenceStore;

public final class MediaEncryptionSettingsUseCaseImpl implements MediaEncryptionSettingsUseCase {
    private final MediaEncryptionPreferenceStore mediaEncryptionPreferences;

    public MediaEncryptionSettingsUseCaseImpl(MediaEncryptionPreferenceStore mediaEncryptionPreferences) {
        this.mediaEncryptionPreferences = mediaEncryptionPreferences;
    }

    @Override public boolean isMediaEncryptionEnabled() {
        return mediaEncryptionPreferences.isMediaEncryptionEnabled();
    }

    @Override public void setMediaEncryptionEnabled(boolean enabled) {
        mediaEncryptionPreferences.setMediaEncryptionEnabled(enabled);
    }
}
