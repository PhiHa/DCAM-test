package com.dvid.dcam.feature.settings.domain;

/** Project-phase feature switches used to hide or disable work that is not ready yet. */
public enum FeatureGate {
    IMAGE_CAPTURE(true),
    VIDEO_CAPTURE(true),
    AUDIO_CAPTURE(true),
    MEDIA_BROWSER(true),
    RECORDING_SETTINGS(true),
    CAMERA_SETTINGS(true),
    AUDIO_SETTINGS(true),
    STORAGE_SETTINGS(true),
    DEVICE_SETTINGS(false),
    GPS(false),
    SECURITY_ENCRYPTION(false),
    CLOUD_NETWORK(false),
    TRANSFER(false),
    VIDEO_STREAMING(false);

    private final boolean defaultEnabled;

    FeatureGate(boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    public boolean defaultEnabled() {
        return defaultEnabled;
    }
}
