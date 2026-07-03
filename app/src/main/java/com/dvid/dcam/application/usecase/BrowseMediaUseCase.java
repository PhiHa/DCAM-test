package com.dvid.dcam.application.usecase;

import com.dvid.dcam.domain.model.MediaEntry;
import com.dvid.dcam.domain.repository.MediaRepository;
import java.util.List;

public final class BrowseMediaUseCase {
    private final MediaRepository repository;

    public BrowseMediaUseCase(MediaRepository repository) { this.repository = repository; }

    public List<MediaEntry> execute(String relativePath) throws Exception {
        return repository.list(relativePath);
    }
}
