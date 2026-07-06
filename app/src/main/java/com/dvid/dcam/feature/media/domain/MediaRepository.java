package com.dvid.dcam.feature.media.domain;

import java.util.List;

public interface MediaRepository {
    List<MediaEntry> list(String relativePath) throws Exception;
}
