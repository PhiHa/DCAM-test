package com.dvid.dcam.feature.capture.application.usecase;

import com.dvid.dcam.feature.capture.application.port.CameraGateway;
import com.dvid.dcam.feature.capture.domain.RecordingMode;

public final class VideoRecordingUseCaseImpl implements VideoRecordingUseCase {
    private final CameraGateway camera;
    private final CaptureEventUseCase events;

    public VideoRecordingUseCaseImpl(CameraGateway camera, CaptureEventUseCase events) {
        this.camera = camera;
        this.events = events;
    }

    @Override public void toggleVideo() {
        if (events.currentMode() == RecordingMode.IDLE) camera.startVideo();
        else camera.stopRecording();
    }

    @Override public void startVideo() {
        camera.startVideo();
    }

    @Override public void startSos() {
        camera.startSos();
    }

    @Override public void stopRecording() {
        camera.stopRecording();
    }

    @Override public void toggleSos() {
        if (events.currentMode() == RecordingMode.SOS) camera.stopRecording();
        else camera.startSos();
    }
}
