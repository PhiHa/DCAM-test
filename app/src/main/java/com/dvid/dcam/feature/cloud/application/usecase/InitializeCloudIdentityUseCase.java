package com.dvid.dcam.feature.cloud.application.usecase;

import com.dvid.dcam.feature.cloud.domain.DeviceCloudIdentity;
import com.dvid.dcam.feature.device.domain.DeviceInfo;

public interface InitializeCloudIdentityUseCase {
    DeviceCloudIdentity execute(DeviceInfo deviceInfo);
}
