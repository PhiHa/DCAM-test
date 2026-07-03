package com.dvid.dcam.data.repository;

import com.dvid.dcam.domain.model.MediaEntry;
import com.dvid.dcam.domain.repository.MediaRepository;
import com.dvid.dcam.domain.service.MediaBrowserService;
import java.util.List;

public final class DefaultMediaRepository implements MediaRepository {
    private final MediaBrowserService service;

    public DefaultMediaRepository(MediaBrowserService service) { this.service = service; }

    @Override public List<MediaEntry> list(String relativePath) throws Exception {
        return service.list(relativePath);
    }
}
