package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.repository.CaptureRepository;

public final class TakePhotoUseCase {
    private final CaptureRepository repository;
    public TakePhotoUseCase(CaptureRepository repository) { this.repository = repository; }
    public void execute() { repository.takePhoto(); }
}
