package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.repository.CaptureRepository;

public final class StartVideoUseCase {
    private final CaptureRepository repository;
    public StartVideoUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.startVideo(); }
}
