package com.dvid.dcam.data.repository;

import com.dvid.dcam.domain.model.DeviceInfo;
import com.dvid.dcam.domain.model.DeviceStatus;
import com.dvid.dcam.domain.repository.DeviceRepository;
import com.dvid.dcam.domain.service.DeviceService;

public final class DefaultDeviceRepository implements DeviceRepository {
    private final DeviceService service;

    public DefaultDeviceRepository(DeviceService service) {
        this.service = service;
    }

    @Override public DeviceInfo readInfo() { return service.read(); }
    @Override public DeviceStatus readStatus() { return service.readStatus(); }
}
