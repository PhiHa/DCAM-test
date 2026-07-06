package com.dvid.dcam.feature.capture.application;

import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class ToggleVideoUseCase {
    private final CaptureRepository repository;
    public ToggleVideoUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.toggleVideo(); }
}
