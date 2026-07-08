package com.dvid.dcam.feature.cloud.application.usecase;

import com.dvid.dcam.feature.cloud.domain.DeviceCloudIdentity;
import com.dvid.dcam.feature.cloud.domain.RemoteConfigSnapshot;

public interface RefreshRemoteConfigUseCase {
    RemoteConfigSnapshot execute(DeviceCloudIdentity identity);
}
