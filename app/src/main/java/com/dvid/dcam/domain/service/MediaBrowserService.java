package com.dvid.dcam.domain.service;

import com.dvid.dcam.domain.model.MediaEntry;
import java.util.List;

/** Read-only access to DCAM-managed media folders. */
public interface MediaBrowserService {
    List<MediaEntry> list(String relativePath) throws Exception;
}
