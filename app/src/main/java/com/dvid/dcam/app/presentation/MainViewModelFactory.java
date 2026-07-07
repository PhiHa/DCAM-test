package com.dvid.dcam.app.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.feature.device.application.usecase.RefreshDeviceStatusUseCase;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.application.usecase.BrowseMediaUseCase;

public final class MainViewModelFactory implements ViewModelProvider.Factory {
    private final DcamConfig config;
    private final DeviceStatus deviceStatus;
    private final RefreshDeviceStatusUseCase refreshDeviceStatus;
    private final BrowseMediaUseCase browseMedia;

    public MainViewModelFactory(
            DcamConfig config,
            DeviceStatus deviceStatus,
            RefreshDeviceStatusUseCase refreshDeviceStatus,
            BrowseMediaUseCase browseMedia) {
        this.config = config;
        this.deviceStatus = deviceStatus;
        this.refreshDeviceStatus = refreshDeviceStatus;
        this.browseMedia = browseMedia;
    }

    @NonNull
    @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(MainViewModel.class)) {
            throw new IllegalArgumentException("Unsupported ViewModel " + modelClass.getName());
        }
        return modelClass.cast(new MainViewModel(
                config, deviceStatus, refreshDeviceStatus, browseMedia));
    }
}
