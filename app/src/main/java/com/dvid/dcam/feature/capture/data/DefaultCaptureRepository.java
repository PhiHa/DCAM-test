package com.dvid.dcam.feature.capture.data;

import com.dvid.dcam.core.config.DcamConfig;
import com.dvid.dcam.feature.capture.domain.AudioService;
import com.dvid.dcam.feature.capture.domain.CameraService;
import com.dvid.dcam.feature.capture.domain.CaptureEventListener;
import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class DefaultCaptureRepository implements CaptureRepository {
    private final CameraService camera;
    private final AudioService audio;
    private final DcamConfig config;

    public DefaultCaptureRepository(CameraService camera, AudioService audio, DcamConfig config) {
        this.camera = camera;
        this.audio = audio;
        this.config = config;
    }

    @Override public void setEventListener(CaptureEventListener listener) {
        camera.setEventListener(listener);
    }

    @Override public void takePhoto() { camera.takePhoto(); }
    @Override public void toggleVideo() { camera.toggleVideo(); }
    @Override public void startVideo() { camera.startVideo(); }
    @Override public void startSos() { camera.startSos(); }
    @Override public void stopRecording() { camera.stopRecording(); }
    @Override public String toggleAudio() { return audio.toggle(config); }
}
