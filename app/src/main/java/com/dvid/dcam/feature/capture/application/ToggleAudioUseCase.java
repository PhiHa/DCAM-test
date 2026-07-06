package com.dvid.dcam.feature.capture.application;

import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class ToggleAudioUseCase {
    private final CaptureRepository repository;
    public ToggleAudioUseCase(CaptureRepository repository) { this.repository = repository; }
    public String execute() { return repository.toggleAudio(); }
}
