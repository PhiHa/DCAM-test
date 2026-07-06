package com.dvid.dcam.feature.capture.application.usecase;

import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.feature.capture.application.port.AudioRecorder;

public final class AudioRecordingUseCaseImpl implements AudioRecordingUseCase {
    private final AudioRecorder audio;
    private final DcamConfig config;

    public AudioRecordingUseCaseImpl(AudioRecorder audio, DcamConfig config) {
        this.audio = audio;
        this.config = config;
    }

    @Override public String toggleAudio() {
        return audio.toggle(config);
    }
}
