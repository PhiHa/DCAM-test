package com.dvid.dcam.feature.media.data;

import com.dvid.dcam.feature.media.domain.MediaBrowserService;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import com.dvid.dcam.feature.media.domain.MediaRepository;
import java.util.List;

public final class DefaultMediaRepository implements MediaRepository {
    private final MediaBrowserService service;

    public DefaultMediaRepository(MediaBrowserService service) { this.service = service; }

    @Override public List<MediaEntry> list(String relativePath) throws Exception {
        return service.list(relativePath);
    }
}
