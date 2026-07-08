package com.dvid.dcam.feature.media.application.usecase;

import com.dvid.dcam.feature.media.domain.MediaEntry;

/** Application entry point for opening a managed media item. */
public interface OpenMediaUseCase {
    boolean execute(MediaEntry entry);
}
