package com.dvid.dcam.feature.device.domain;

/** Immutable operational status safe for presentation and metadata workflows. */
public final class DeviceStatus {
    public static final int UNKNOWN_BATTERY_PERCENT = -1;
    public static final long UNKNOWN_STORAGE_BYTES = -1L;

    private final int batteryPercent;
    private final long availableStorageBytes;
    private final CapabilityStatus gpsStatus;

    public DeviceStatus(int batteryPercent, long availableStorageBytes, CapabilityStatus gpsStatus) {
        this.batteryPercent = batteryPercent;
        this.availableStorageBytes = availableStorageBytes;
        this.gpsStatus = gpsStatus == null ? CapabilityStatus.UNKNOWN : gpsStatus;
    }

    public static DeviceStatus unknown() {
        return new DeviceStatus(UNKNOWN_BATTERY_PERCENT, UNKNOWN_STORAGE_BYTES, CapabilityStatus.UNKNOWN);
    }

    public int getBatteryPercent() { return batteryPercent; }
    public long getAvailableStorageBytes() { return availableStorageBytes; }
    public CapabilityStatus getGpsStatus() { return gpsStatus; }
}
