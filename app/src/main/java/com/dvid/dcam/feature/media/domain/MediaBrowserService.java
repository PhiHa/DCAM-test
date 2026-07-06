package com.dvid.dcam.feature.media.domain;

import java.util.List;

/** Read-only access to DCAM-managed media folders. */
public interface MediaBrowserService {
    List<MediaEntry> list(String relativePath) throws Exception;
}
