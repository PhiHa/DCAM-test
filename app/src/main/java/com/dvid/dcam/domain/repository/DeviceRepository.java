package com.dvid.dcam.domain.repository;

import com.dvid.dcam.domain.model.DeviceInfo;
import com.dvid.dcam.domain.model.DeviceStatus;

public interface DeviceRepository {
    DeviceInfo readInfo();
    DeviceStatus readStatus();
}
