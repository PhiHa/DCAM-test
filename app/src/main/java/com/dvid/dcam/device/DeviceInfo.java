package com.dvid.dcam.device;

public final class DeviceInfo {
    private final String hardwareId;
    private final String model;

    public DeviceInfo(String hardwareId, String model) {
        this.hardwareId = hardwareId;
        this.model = model;
    }

    public String getHardwareId() { return hardwareId; }
    public String getModel() { return model; }
}
