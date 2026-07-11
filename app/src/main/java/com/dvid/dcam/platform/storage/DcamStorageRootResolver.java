package com.dvid.dcam.platform.storage;

import com.dvid.dcam.feature.settings.domain.StorageMode;
import java.util.List;

/** External-first media-root resolver. Fallback is decided before capture, never mid-file. */
public final class DcamStorageRootResolver {
    private final DcamStorageCapacityPolicy capacityPolicy;

    public DcamStorageRootResolver(DcamStorageCapacityPolicy capacityPolicy) {
        this.capacityPolicy = capacityPolicy;
    }

    public DcamStorageResolution resolve(
            StorageMode requestedMode,
            DcamStorageCandidate internal,
            List<DcamStorageCandidate> externalCandidates) {
        StorageMode requested = requestedMode == null ? StorageMode.AUTO : requestedMode;
        if (requested == StorageMode.INTERNAL) {
            return new DcamStorageResolution(requested, StorageMode.INTERNAL, internal.getRoot(), false);
        }

        for (DcamStorageCandidate external : externalCandidates) {
            if (external.getMode() == StorageMode.EXTERNAL
                    && external.check(capacityPolicy).isReady()) {
                return new DcamStorageResolution(requested, StorageMode.EXTERNAL,
                        external.getRoot(), false);
            }
        }

        return new DcamStorageResolution(
                requested, StorageMode.INTERNAL, internal.getRoot(), true);
    }
}
