package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_identity")
public final class DeviceIdentityEntity {
    @PrimaryKey
    public int id;
    public String dcamCloudDeviceId;
    @NonNull
    public String hardwareId;
    public String serialNumber;
    @NonNull
    @ColumnInfo(defaultValue = "'PROVISIONING_REQUIRED'")
    public String provisioningState;
    public String firebaseInstallationId;
    @ColumnInfo(defaultValue = "0")
    public long updatedAt;

    public DeviceIdentityEntity(
            int id,
            String dcamCloudDeviceId,
            @NonNull String hardwareId,
            String serialNumber,
            @NonNull String provisioningState,
            String firebaseInstallationId,
            long updatedAt) {
        this.id = id;
        this.dcamCloudDeviceId = dcamCloudDeviceId;
        this.hardwareId = hardwareId;
        this.serialNumber = serialNumber;
        this.provisioningState = provisioningState;
        this.firebaseInstallationId = firebaseInstallationId;
        this.updatedAt = updatedAt;
    }
}
