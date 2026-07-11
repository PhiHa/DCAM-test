package com.dvid.dcam.platform.storage;

import com.dvid.dcam.feature.settings.domain.StorageMode;
import java.io.File;

/** Requested policy and physical media root selected for the next app runtime. */
public final class DcamStorageResolution {
    private final StorageMode requestedMode;
    private final StorageMode resolvedMode;
    private final File root;
    private final boolean fallback;

    public DcamStorageResolution(
            StorageMode requestedMode, StorageMode resolvedMode, File root, boolean fallback) {
        this.requestedMode = requestedMode;
        this.resolvedMode = resolvedMode;
        this.root = root;
        this.fallback = fallback;
    }

    public StorageMode getRequestedMode() { return requestedMode; }
    public StorageMode getResolvedMode() { return resolvedMode; }
    public File getRoot() { return root; }
    public boolean isFallback() { return fallback; }
}
