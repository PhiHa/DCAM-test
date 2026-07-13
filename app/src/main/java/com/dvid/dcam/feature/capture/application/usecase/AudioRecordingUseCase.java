package com.dvid.dcam.feature.capture.application.usecase;

/** Application entry point for audio recording operations. */
public interface AudioRecordingUseCase {
    String toggleAudio();
    boolean isAudioRecording();
}
