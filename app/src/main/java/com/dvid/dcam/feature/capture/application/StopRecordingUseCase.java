package com.dvid.dcam.feature.capture.application;

import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class StopRecordingUseCase {
    private final CaptureRepository repository;
    public StopRecordingUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.stopRecording(); }
}
