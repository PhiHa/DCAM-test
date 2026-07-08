package com.dvid.dcam.feature.media.application.usecase;

import com.dvid.dcam.feature.media.application.port.MediaOpener;
import com.dvid.dcam.feature.media.domain.MediaEntry;

public final class OpenMediaUseCaseImpl implements OpenMediaUseCase {
    private final MediaOpener mediaOpener;

    public OpenMediaUseCaseImpl(MediaOpener mediaOpener) {
        this.mediaOpener = mediaOpener;
    }

    @Override public boolean execute(MediaEntry entry) {
        return entry != null
                && !entry.isDirectory()
                && mediaOpener.open(entry.getRelativePath(), entry.getMimeType());
    }
}
