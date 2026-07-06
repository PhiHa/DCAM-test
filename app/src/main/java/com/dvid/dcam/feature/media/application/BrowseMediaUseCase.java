package com.dvid.dcam.feature.media.application;

import com.dvid.dcam.feature.media.domain.MediaEntry;
import com.dvid.dcam.feature.media.domain.MediaRepository;
import java.util.List;

public final class BrowseMediaUseCase {
    private final MediaRepository repository;

    public BrowseMediaUseCase(MediaRepository repository) { this.repository = repository; }

    public List<MediaEntry> execute(String relativePath) throws Exception {
        return repository.list(relativePath);
    }
}
