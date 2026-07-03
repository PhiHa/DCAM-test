package com.dvid.dcam.domain.repository;

import com.dvid.dcam.domain.event.CaptureEventListener;

/** Coordinates capture capabilities without exposing platform implementations. */
public interface CaptureRepository {
    void setEventListener(CaptureEventListener listener);
    void takePhoto();
    void toggleVideo();
    void startVideo();
    void startSos();
    void stopRecording();
    String toggleAudio();
}
