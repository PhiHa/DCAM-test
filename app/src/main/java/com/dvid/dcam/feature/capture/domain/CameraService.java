package com.dvid.dcam.feature.capture.domain;


/** Camera capability required by application workflows, independent of CameraX/vendor APIs. */
public interface CameraService {
    void setEventListener(CaptureEventListener listener);
    void bindIfPermitted();
    void takePhoto();
    void toggleVideo();
    void startVideo();
    void startSos();
    void stopRecording();
    void release();
}
