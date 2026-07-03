package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.repository.CaptureRepository;

public final class ToggleAudioUseCase {
    private final CaptureRepository repository;
    public ToggleAudioUseCase(CaptureRepository repository) { this.repository = repository; }
    public String execute() { return repository.toggleAudio(); }
}
