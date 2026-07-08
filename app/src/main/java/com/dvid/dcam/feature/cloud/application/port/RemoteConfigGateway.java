package com.dvid.dcam.feature.cloud.application.port;

import com.dvid.dcam.feature.cloud.domain.DeviceCloudIdentity;
import com.dvid.dcam.feature.cloud.domain.RemoteConfigSnapshot;

/** Provider boundary for fetching remote config by approved device identity. */
public interface RemoteConfigGateway {
    RemoteConfigSnapshot fetch(DeviceCloudIdentity identity);
}
