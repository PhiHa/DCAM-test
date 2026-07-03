package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.repository.CaptureRepository;

public final class StopRecordingUseCase {
    private final CaptureRepository repository;
    public StopRecordingUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.stopRecording(); }
}
