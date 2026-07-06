package com.dvid.dcam.feature.capture.application;

import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class StartVideoUseCase {
    private final CaptureRepository repository;
    public StartVideoUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.startVideo(); }
}
