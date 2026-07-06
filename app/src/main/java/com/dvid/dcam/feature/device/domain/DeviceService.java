package com.dvid.dcam.feature.device.domain;


/** Device identity/status boundary. Status and capability models will expand in Phase 2. */
public interface DeviceService {
    DeviceInfo read();
    DeviceStatus readStatus();
}
