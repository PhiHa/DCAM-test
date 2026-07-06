package com.dvid.dcam.app.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.CaptureEventUseCase;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCase;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCase;
import com.dvid.dcam.feature.device.application.usecase.RefreshDeviceStatusUseCase;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.application.usecase.BrowseMediaUseCase;

public final class MainViewModelFactory implements ViewModelProvider.Factory {
    private final DcamConfig config;
    private final DeviceStatus deviceStatus;
    private final PhotoCaptureUseCase photoCapture;
    private final VideoRecordingUseCase videoRecording;
    private final AudioRecordingUseCase audioRecording;
    private final CaptureEventUseCase captureEvents;
    private final RefreshDeviceStatusUseCase refreshDeviceStatus;
    private final BrowseMediaUseCase browseMedia;

    public MainViewModelFactory(
            DcamConfig config,
            DeviceStatus deviceStatus,
            PhotoCaptureUseCase photoCapture,
            VideoRecordingUseCase videoRecording,
            AudioRecordingUseCase audioRecording,
            CaptureEventUseCase captureEvents,
            RefreshDeviceStatusUseCase refreshDeviceStatus,
            BrowseMediaUseCase browseMedia) {
        this.config = config;
        this.deviceStatus = deviceStatus;
        this.photoCapture = photoCapture;
        this.videoRecording = videoRecording;
        this.audioRecording = audioRecording;
        this.captureEvents = captureEvents;
        this.refreshDeviceStatus = refreshDeviceStatus;
        this.browseMedia = browseMedia;
    }

    @NonNull
    @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(MainViewModel.class)) {
            throw new IllegalArgumentException("Unsupported ViewModel " + modelClass.getName());
        }
        return modelClass.cast(new MainViewModel(
                config, deviceStatus, photoCapture, videoRecording, audioRecording, captureEvents,
                refreshDeviceStatus, browseMedia));
    }
}
