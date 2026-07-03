package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.repository.CaptureRepository;

public final class ToggleVideoUseCase {
    private final CaptureRepository repository;
    public ToggleVideoUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.toggleVideo(); }
}
