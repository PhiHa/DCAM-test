package com.dvid.dcam.platform.cloud;

import com.dvid.dcam.feature.cloud.application.port.RemoteConfigGateway;
import com.dvid.dcam.feature.cloud.domain.DeviceCloudIdentity;
import com.dvid.dcam.feature.cloud.domain.RemoteConfigSnapshot;

/** Local-only remote config gateway until WebServer payload details are approved. */
public final class NoOpRemoteConfigGatewayImpl implements RemoteConfigGateway {
    @Override public RemoteConfigSnapshot fetch(DeviceCloudIdentity identity) {
        return RemoteConfigSnapshot.none();
    }
}
