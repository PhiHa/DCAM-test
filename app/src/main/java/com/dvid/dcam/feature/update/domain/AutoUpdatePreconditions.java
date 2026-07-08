package com.dvid.dcam.feature.update.domain;

/** Minimal local representation of the approved AutoUpdate runtime guards. */
public final class AutoUpdatePreconditions {
    private final boolean provisioned;
    private final boolean recordingIdle;
    private final boolean emergencyIdle;
    private final boolean finalizingIdle;
    private final boolean batteryReady;
    private final boolean storageReady;
    private final boolean networkReady;

    public AutoUpdatePreconditions(
            boolean provisioned,
            boolean recordingIdle,
            boolean emergencyIdle,
            boolean finalizingIdle,
            boolean batteryReady,
            boolean storageReady,
            boolean networkReady) {
        this.provisioned = provisioned;
        this.recordingIdle = recordingIdle;
        this.emergencyIdle = emergencyIdle;
        this.finalizingIdle = finalizingIdle;
        this.batteryReady = batteryReady;
        this.storageReady = storageReady;
        this.networkReady = networkReady;
    }

    public boolean canCheckForUpdate() {
        return provisioned && recordingIdle && emergencyIdle && finalizingIdle
                && batteryReady && storageReady && networkReady;
    }

    public boolean isProvisioned() { return provisioned; }
    public boolean isRecordingIdle() { return recordingIdle; }
    public boolean isEmergencyIdle() { return emergencyIdle; }
    public boolean isFinalizingIdle() { return finalizingIdle; }
    public boolean isBatteryReady() { return batteryReady; }
    public boolean isStorageReady() { return storageReady; }
    public boolean isNetworkReady() { return networkReady; }
}
