package com.dvid.dcam.feature.capture.application;

import com.dvid.dcam.feature.capture.domain.CaptureRepository;

public final class TakePhotoUseCase {
    private final CaptureRepository repository;
    public TakePhotoUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.takePhoto(); }
}
