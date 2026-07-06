package com.dvid.dcam.feature.capture.application;

import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class StartSosUseCase {
    private final CaptureRepository repository;
    public StartSosUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.startSos(); }
}
