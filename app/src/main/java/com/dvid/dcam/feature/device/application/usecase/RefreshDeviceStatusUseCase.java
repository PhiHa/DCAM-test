package com.dvid.dcam.feature.device.application.usecase;

import com.dvid.dcam.feature.device.domain.DeviceStatus;

/** Application entry point for refreshing device capability status. */
public interface RefreshDeviceStatusUseCase {
    DeviceStatus execute();
}
