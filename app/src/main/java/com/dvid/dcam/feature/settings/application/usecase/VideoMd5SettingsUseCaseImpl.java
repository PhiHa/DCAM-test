package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.application.port.VideoMd5PreferenceStore;

public final class VideoMd5SettingsUseCaseImpl implements VideoMd5SettingsUseCase {
    private final VideoMd5PreferenceStore preferences;

    public VideoMd5SettingsUseCaseImpl(VideoMd5PreferenceStore preferences) {
        this.preferences = preferences;
    }

    @Override public boolean isVideoMd5Enabled() { return preferences.isVideoMd5Enabled(); }

    @Override public void setVideoMd5Enabled(boolean enabled) {
        preferences.setVideoMd5Enabled(enabled);
    }
}
