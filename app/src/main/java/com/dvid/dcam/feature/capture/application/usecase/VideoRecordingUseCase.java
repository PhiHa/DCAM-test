package com.dvid.dcam.feature.capture.application.usecase;

/** Application entry point for the related video/SOS recording operations. */
public interface VideoRecordingUseCase {
    void toggleVideo();
    void startVideo();
    void startSos();
    void stopRecording();
    void toggleSos();
}
