package com.dvid.dcam.feature.device.application.usecase;

import com.dvid.dcam.feature.device.application.port.DeviceRepository;
import com.dvid.dcam.feature.device.domain.DeviceStatus;

public final class RefreshDeviceStatusUseCaseImpl implements RefreshDeviceStatusUseCase {
    private final DeviceRepository repository;

    public RefreshDeviceStatusUseCaseImpl(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override public DeviceStatus execute() {
        return repository.readStatus();
    }
}
