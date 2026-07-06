package com.dvid.dcam.feature.capture.application.usecase;

import com.dvid.dcam.feature.capture.application.port.CameraGateway;

public final class PhotoCaptureUseCaseImpl implements PhotoCaptureUseCase {
    private final CameraGateway camera;

    public PhotoCaptureUseCaseImpl(CameraGateway camera) {
        this.camera = camera;
    }

    @Override public void takePhoto() {
        camera.takePhoto();
    }
}
