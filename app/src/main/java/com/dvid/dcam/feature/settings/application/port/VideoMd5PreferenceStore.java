package com.dvid.dcam.feature.settings.application.port;

public interface VideoMd5PreferenceStore {
    boolean isVideoMd5Enabled();
    void setVideoMd5Enabled(boolean enabled);
}
