package com.dvid.dcam.feature.cloud.domain;

/** Local provisioning state used before cloud/provider details are implemented. */
public enum ProvisioningState {
    PROVISIONING_REQUIRED,
    PROVISIONED,
    RECOVERY_REQUIRED
}
