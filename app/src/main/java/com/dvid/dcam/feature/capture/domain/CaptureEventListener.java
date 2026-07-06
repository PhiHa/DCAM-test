package com.dvid.dcam.feature.capture.domain;


/** Receives domain-level capture results after SDK-specific events are mapped. */
public interface CaptureEventListener {
    CaptureEventListener NONE = new CaptureEventListener() {};

    default void onRecordingStarted(RecordingMode mode, String fileName) {}
    default void onRecordingCompleted(String fileName) {}
    default void onPhotoSaved(String fileName) {}
    default void onCaptureError(String operation, String message) {}
}
