package com.dvid.dcam.feature.device.domain;

/** Stable device identity exposed to application and diagnostics layers. */
public final class DeviceInfo {
    private final String hardwareId;
    private final String model;
    private final String serialNumber;

    public DeviceInfo(String hardwareId, String model) {
        this(hardwareId, model, null);
    }

    public DeviceInfo(String hardwareId, String model, String serialNumber) {
        this.hardwareId = hardwareId;
        this.model = model;
        this.serialNumber = serialNumber;
    }

    public String getHardwareId() { return hardwareId; }
    public String getModel() { return model; }
    public String getSerialNumber() { return serialNumber; }
}
