package com.dvid.dcam.feature.media.application.usecase;

import com.dvid.dcam.feature.media.application.port.MediaRepository;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import java.util.List;

public final class BrowseMediaUseCaseImpl implements BrowseMediaUseCase {
    private final MediaRepository repository;

    public BrowseMediaUseCaseImpl(MediaRepository repository) {
        this.repository = repository;
    }

    @Override public List<MediaEntry> execute(String relativePath) throws Exception {
        return repository.list(relativePath);
    }
}
