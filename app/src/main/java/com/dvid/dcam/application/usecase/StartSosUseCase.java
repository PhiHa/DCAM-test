package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.repository.CaptureRepository;

public final class StartSosUseCase {
    private final CaptureRepository repository;
    public StartSosUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.startSos(); }
}
