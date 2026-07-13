package com.dvid.dcam.feature.settings.application.usecase;

public interface VideoMd5SettingsUseCase {
    boolean isVideoMd5Enabled();
    void setVideoMd5Enabled(boolean enabled);
}
