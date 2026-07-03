package com.dvid.dcam.domain.event;

import com.dvid.dcam.domain.model.RecordingMode;

/** Receives domain-level capture results after SDK-specific events are mapped. */
public interface CaptureEventListener {
    CaptureEventListener NONE = new CaptureEventListener() {};

    default void onRecordingStarted(RecordingMode mode, String fileName) {}
    default void onRecordingCompleted(String fileName) {}
    default void onPhotoSaved(String fileName) {}
    default void onCaptureError(String operation, String message) {}
}
