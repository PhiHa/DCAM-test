package com.dvid.dcam.feature.media.application.usecase;

import com.dvid.dcam.feature.media.domain.MediaEntry;
import java.util.List;

/** Application entry point for browsing managed media. */
public interface BrowseMediaUseCase {
    List<MediaEntry> execute(String relativePath) throws Exception;
}
