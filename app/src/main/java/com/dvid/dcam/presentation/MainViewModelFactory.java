package com.dvid.dcam.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.dvid.dcam.domain.model.DcamConfig;
import com.dvid.dcam.domain.model.DeviceStatus;
import com.dvid.dcam.domain.repository.DeviceRepository;
import com.dvid.dcam.domain.repository.MediaRepository;

public final class MainViewModelFactory implements ViewModelProvider.Factory {
    private final DcamConfig config;
    private final DeviceStatus deviceStatus;
    private final DeviceRepository deviceRepository;
    private final MediaRepository mediaRepository;

    public MainViewModelFactory(
            DcamConfig config, DeviceStatus deviceStatus, DeviceRepository deviceRepository,
            MediaRepository mediaRepository) {
        this.config = config;
        this.deviceStatus = deviceStatus;
        this.deviceRepository = deviceRepository;
        this.mediaRepository = mediaRepository;
    }

    @NonNull
    @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(MainViewModel.class)) {
            throw new IllegalArgumentException("Unsupported ViewModel " + modelClass.getName());
        }
        return modelClass.cast(new MainViewModel(
                config, deviceStatus, deviceRepository, mediaRepository));
    }
}
