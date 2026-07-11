package com.dvid.dcam.feature.settings.presentation;
import java.util.List;

/**
 * In-memory settings state used until operational settings persistence is connected.
 * The UI still follows normal get/update/render flow.
 */
public final class DemoSettingsState {
    private static final List<String> RECORD_RESOLUTIONS = List.of("720p", "1080p", "1440p", "4K");
    private static final List<String> STORAGE_OPTIONS = List.of("Internal", "External", "Auto");
    private static final List<String> USB_OPTIONS = List.of("Off", "Password", "Admin only");

    private String recordResolution = "1080p";
    private int segmentLengthMinutes = 5;
    private boolean loopRecordingEnabled = true;
    private boolean videoEncryptionEnabled;
    private int defaultStorageIndex;
    private int lowStorageWarningGb = 2;
    private boolean recycleOldRecordings = true;
    private boolean protectSettingsMenu = true;
    private int usbAccessIndex;
    private boolean fullScreenDisplay;
    private boolean statusLightsEnabled = true;

    public DemoSettingsState(boolean videoEncryptionEnabled, int defaultStorageIndex) {
        this.videoEncryptionEnabled = videoEncryptionEnabled;
        this.defaultStorageIndex = clamp(defaultStorageIndex, STORAGE_OPTIONS.size());
    }

    public void select(SettingId id, int selectedIndex) {
        switch (id) {
            case RECORD_RESOLUTION:
                recordResolution = RECORD_RESOLUTIONS.get(clamp(selectedIndex, RECORD_RESOLUTIONS.size()));
                break;
            case DEFAULT_STORAGE:
                defaultStorageIndex = clamp(selectedIndex, STORAGE_OPTIONS.size());
                break;
            case USB_ACCESS_PROTECTION:
                usbAccessIndex = clamp(selectedIndex, USB_OPTIONS.size());
                break;
            default:
                throw new IllegalArgumentException("Setting " + id + " is not a choice");
        }
    }

    public void updateNumber(SettingId id, int value) {
        switch (id) {
            case VIDEO_SEGMENT_LENGTH_MINUTES:
                segmentLengthMinutes = clamp(value, 1, 30);
                break;
            case LOW_STORAGE_WARNING_GB:
                lowStorageWarningGb = clamp(value, 1, 20);
                break;
            default:
                throw new IllegalArgumentException("Setting " + id + " is not numeric");
        }
    }

    public void updateBoolean(SettingId id, boolean checked) {
        switch (id) {
            case LOOP_RECORDING:
                loopRecordingEnabled = checked;
                break;
            case RECYCLE_OLD_RECORDINGS:
                recycleOldRecordings = checked;
                break;
            case ENCRYPT_VIDEO_FILES:
                videoEncryptionEnabled = checked;
                break;
            case PROTECT_SETTINGS_MENU:
                protectSettingsMenu = checked;
                break;
            case FULL_SCREEN_DISPLAY:
                fullScreenDisplay = checked;
                break;
            case STATUS_LIGHTS:
                statusLightsEnabled = checked;
                break;
            default:
                throw new IllegalArgumentException("Setting " + id + " is not boolean");
        }
    }

    public void setVideoEncryptionEnabled(boolean enabled) {
        videoEncryptionEnabled = enabled;
    }

    public SettingsScreenModel recording() {
        return new SettingsScreenModel(List.of(new SettingsSection("Recording", List.of(
                SettingItem.choice(SettingId.RECORD_RESOLUTION, "Record resolution",
                        RECORD_RESOLUTIONS, RECORD_RESOLUTIONS.indexOf(recordResolution)),
                SettingItem.slider(SettingId.VIDEO_SEGMENT_LENGTH_MINUTES, "Video segment length",
                        1, 30, segmentLengthMinutes, "min"),
                SettingItem.checkbox(SettingId.LOOP_RECORDING, "Loop recording", loopRecordingEnabled)))));
    }

    public SettingsScreenModel storage() {
        return new SettingsScreenModel(List.of(new SettingsSection("Storage", List.of(
                SettingItem.radio(SettingId.DEFAULT_STORAGE, "Default storage",
                        STORAGE_OPTIONS, defaultStorageIndex),
                SettingItem.slider(SettingId.LOW_STORAGE_WARNING_GB, "Low-storage warning",
                        1, 20, lowStorageWarningGb, "GB"),
                SettingItem.checkbox(SettingId.RECYCLE_OLD_RECORDINGS,
                        "Recycle old recordings", recycleOldRecordings)))));
    }

    public SettingsScreenModel security() {
        return new SettingsScreenModel(List.of(
                new SettingsSection("Operator account", List.of(
                        SettingItem.action(SettingId.CHANGE_OPERATOR_ID, "Change ID"),
                        SettingItem.action(SettingId.CHANGE_OPERATOR_PASSWORD, "Change password"),
                        SettingItem.action(SettingId.LOGOUT, "Log out"))),
                new SettingsSection("Security", List.of(
                        SettingItem.checkbox(SettingId.ENCRYPT_VIDEO_FILES,
                                "Encrypt media files", videoEncryptionEnabled),
                        SettingItem.checkbox(SettingId.PROTECT_SETTINGS_MENU,
                                "Protect settings menu", protectSettingsMenu),
                        SettingItem.choice(SettingId.USB_ACCESS_PROTECTION,
                                "USB access protection", USB_OPTIONS, usbAccessIndex)))));
    }

    public SettingsScreenModel device() {
        return new SettingsScreenModel(List.of(new SettingsSection("Device", List.of(
                SettingItem.checkbox(SettingId.FULL_SCREEN_DISPLAY,
                        "Full-screen display", fullScreenDisplay),
                SettingItem.checkbox(SettingId.STATUS_LIGHTS,
                        "Status lights", statusLightsEnabled)))));
    }

    public SettingsScreenModel readOnly(String[] fallbackLabels) {
        SettingItem[] items = new SettingItem[fallbackLabels.length];
        for (int i = 0; i < fallbackLabels.length; i++) {
            items[i] = SettingItem.text(fallbackLabels[i], "Pending");
        }
        return new SettingsScreenModel(List.of(new SettingsSection("Available settings", List.of(items))));
    }

    private static int clamp(int selectedIndex, int size) {
        if (size <= 0) return 0;
        return Math.max(0, Math.min(size - 1, selectedIndex));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
