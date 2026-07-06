package com.dvid.dcam.feature.device.domain;


public interface DeviceRepository {
    DeviceInfo readInfo();
    DeviceStatus readStatus();
}
