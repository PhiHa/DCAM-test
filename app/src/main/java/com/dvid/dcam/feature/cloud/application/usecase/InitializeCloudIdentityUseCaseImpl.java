package com.dvid.dcam.feature.cloud.application.usecase;

import com.dvid.dcam.feature.cloud.application.port.DeviceIdentityRepository;
import com.dvid.dcam.feature.cloud.domain.DeviceCloudIdentity;
import com.dvid.dcam.feature.device.domain.DeviceInfo;

public final class InitializeCloudIdentityUseCaseImpl implements InitializeCloudIdentityUseCase {
    private final DeviceIdentityRepository repository;

    public InitializeCloudIdentityUseCaseImpl(DeviceIdentityRepository repository) {
        this.repository = repository;
    }

    @Override public DeviceCloudIdentity execute(DeviceInfo deviceInfo) {
        return repository.restoreOrCreate(deviceInfo);
    }
}
