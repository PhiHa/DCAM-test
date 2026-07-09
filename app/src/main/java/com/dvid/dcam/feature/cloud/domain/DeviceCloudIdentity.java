package com.dvid.dcam.feature.cloud.domain;

/** Cloud identity snapshot stored locally for startup and recovery decisions. */
public final class DeviceCloudIdentity {
    private final String dcamCloudDeviceId;
    private final String hardwareId;
    private final String serialNumber;
    private final ProvisioningState provisioningState;
    private final String firebaseInstallationId;

    public DeviceCloudIdentity(
            String dcamCloudDeviceId,
            String hardwareId,
            String serialNumber,
            ProvisioningState provisioningState,
            String firebaseInstallationId) {
        this.dcamCloudDeviceId = emptyToNull(dcamCloudDeviceId);
        this.hardwareId = required(hardwareId, "hardwareId");
        this.serialNumber = emptyToNull(serialNumber);
        this.provisioningState = provisioningState == null
                ? ProvisioningState.PROVISIONING_REQUIRED : provisioningState;
        this.firebaseInstallationId = emptyToNull(firebaseInstallationId);
    }

    public String getDcamCloudDeviceId() { return dcamCloudDeviceId; }
    public String getHardwareId() { return hardwareId; }
    public String getSerialNumber() { return serialNumber; }
    public ProvisioningState getProvisioningState() { return provisioningState; }
    public String getFirebaseInstallationId() { return firebaseInstallationId; }

    public boolean requiresProvisioning() {
        return provisioningState != ProvisioningState.PROVISIONED
                || dcamCloudDeviceId == null || dcamCloudDeviceId.isBlank();
    }

    private static String required(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.trim();
    }

    private static String emptyToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
