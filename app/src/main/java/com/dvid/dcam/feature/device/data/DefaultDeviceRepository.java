package com.dvid.dcam.feature.device.data;

import com.dvid.dcam.feature.device.domain.DeviceInfo;
import com.dvid.dcam.feature.device.domain.DeviceRepository;
import com.dvid.dcam.feature.device.domain.DeviceService;
import com.dvid.dcam.feature.device.domain.DeviceStatus;

public final class DefaultDeviceRepository implements DeviceRepository {
    private final DeviceService service;

    public DefaultDeviceRepository(DeviceService service) {
        this.service = service;
    }

    @Override public DeviceInfo readInfo() { return service.read(); }
    @Override public DeviceStatus readStatus() { return service.readStatus(); }
}
