package com.dvid.dcam.feature.capture.application;

/** Application commands shared by touch UI and physical BodyCamera buttons. */
public interface DcamCommandHandler {
    void takePhoto();
    void toggleVideo();
    void startVideo();
    void stopRecording();
    void toggleAudio();
    void toggleSos();
}
