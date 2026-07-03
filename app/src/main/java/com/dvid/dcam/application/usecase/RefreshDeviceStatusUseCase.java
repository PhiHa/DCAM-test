package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.model.DeviceStatus;
import com.dvid.dcam.domain.repository.DeviceRepository;

public final class RefreshDeviceStatusUseCase {
    private final DeviceRepository repository;

    public RefreshDeviceStatusUseCase(DeviceRepository repository) {
        this.repository = repository;
    }

    public DeviceStatus execute() { return repository.readStatus(); }
}
