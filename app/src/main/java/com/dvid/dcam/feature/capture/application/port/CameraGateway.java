package com.dvid.dcam.feature.capture.application.port;

/** Camera capability required by application workflows, independent of CameraX/vendor APIs. */
public interface CameraGateway {
    void takePhoto();
    void startVideo();
    void startSos();
    void stopRecording();
}
