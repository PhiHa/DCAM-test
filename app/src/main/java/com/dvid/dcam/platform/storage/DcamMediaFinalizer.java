package com.dvid.dcam.platform.storage;

import java.io.File;
import java.io.IOException;

/** Filesystem readiness boundary: validate staging, publish safely, then remove staging. */
public final class DcamMediaFinalizer {
    private final DcamStorage storage;
    private final DcamMediaPublisher publisher;

    public DcamMediaFinalizer(DcamStorage storage) {
        this(storage, new DcamMediaPublisher());
    }

    DcamMediaFinalizer(DcamStorage storage, DcamMediaPublisher publisher) {
        this.storage = storage;
        this.publisher = publisher;
    }

    public File finalizeMedia(DcamMediaFile mediaFile) throws IOException {
        File staging = mediaFile.getFile();
        DcamMediaPublisher.validate(staging, "staging");
        File target = storage.finalFile(mediaFile);
        try {
            File published = publisher.publish(staging, target);
            // A leftover staging duplicate is safe; never roll back a valid final file for cleanup failure.
            try { java.nio.file.Files.deleteIfExists(staging.toPath()); } catch (IOException ignored) { }
            return published;
        } catch (IOException | RuntimeException failure) {
            throw asIOException("Media publication failed", failure);
        }
    }

    private static IOException asIOException(String prefix, Throwable failure) {
        return new IOException(prefix + ": " + message(failure), failure);
    }

    private static String message(Throwable failure) {
        return failure.getMessage() == null ? failure.getClass().getSimpleName() : failure.getMessage();
    }
}
