package com.dvid.dcam.feature.settings.presentation;

import com.dvid.dcam.feature.settings.domain.FeatureGate;
import java.util.List;
import java.util.function.Predicate;

/** Presentation model for the hidden project-phase feature gate screen. */
public final class FeatureGateSettingsScreenModel {
    public SettingsScreenModel build(Predicate<FeatureGate> isEnabled) {
        return new SettingsScreenModel(List.of(
                new SettingsSection("Capture commands", List.of(
                        toggle(SettingId.FEATURE_IMAGE_CAPTURE, "Image capture", isEnabled),
                        toggle(SettingId.FEATURE_VIDEO_CAPTURE, "Video recording", isEnabled),
                        toggle(SettingId.FEATURE_AUDIO_CAPTURE, "Audio recording", isEnabled))),
                new SettingsSection("Local app surfaces", List.of(
                        toggle(SettingId.FEATURE_MEDIA_BROWSER, "Files browser", isEnabled),
                        toggle(SettingId.FEATURE_RECORDING_SETTINGS, "Recording settings", isEnabled),
                        toggle(SettingId.FEATURE_CAMERA_SETTINGS, "Camera settings", isEnabled),
                        toggle(SettingId.FEATURE_AUDIO_SETTINGS, "Audio settings", isEnabled),
                        toggle(SettingId.FEATURE_STORAGE_SETTINGS, "Storage settings", isEnabled),
                        toggle(SettingId.FEATURE_DEVICE_SETTINGS, "Device settings", isEnabled))),
                new SettingsSection("Later-phase features", List.of(
                        toggle(SettingId.FEATURE_GPS, "GPS", isEnabled),
                        toggle(SettingId.FEATURE_SECURITY_ENCRYPTION,
                                "Security and encryption", isEnabled),
                        toggle(SettingId.FEATURE_CLOUD_NETWORK, "Cloud / network", isEnabled),
                        toggle(SettingId.FEATURE_TRANSFER, "Transfer", isEnabled),
                        toggle(SettingId.FEATURE_VIDEO_STREAMING, "Video streaming", isEnabled)))));
    }

    public FeatureGate featureFor(SettingId id) {
        switch (id) {
            case FEATURE_IMAGE_CAPTURE: return FeatureGate.IMAGE_CAPTURE;
            case FEATURE_VIDEO_CAPTURE: return FeatureGate.VIDEO_CAPTURE;
            case FEATURE_AUDIO_CAPTURE: return FeatureGate.AUDIO_CAPTURE;
            case FEATURE_MEDIA_BROWSER: return FeatureGate.MEDIA_BROWSER;
            case FEATURE_RECORDING_SETTINGS: return FeatureGate.RECORDING_SETTINGS;
            case FEATURE_CAMERA_SETTINGS: return FeatureGate.CAMERA_SETTINGS;
            case FEATURE_AUDIO_SETTINGS: return FeatureGate.AUDIO_SETTINGS;
            case FEATURE_STORAGE_SETTINGS: return FeatureGate.STORAGE_SETTINGS;
            case FEATURE_DEVICE_SETTINGS: return FeatureGate.DEVICE_SETTINGS;
            case FEATURE_GPS: return FeatureGate.GPS;
            case FEATURE_SECURITY_ENCRYPTION: return FeatureGate.SECURITY_ENCRYPTION;
            case FEATURE_CLOUD_NETWORK: return FeatureGate.CLOUD_NETWORK;
            case FEATURE_TRANSFER: return FeatureGate.TRANSFER;
            case FEATURE_VIDEO_STREAMING: return FeatureGate.VIDEO_STREAMING;
            default: return null;
        }
    }

    private SettingItem toggle(
            SettingId id, String label, Predicate<FeatureGate> isEnabled) {
        FeatureGate feature = featureFor(id);
        return SettingItem.checkbox(id, label, feature != null && isEnabled.test(feature));
    }
}
