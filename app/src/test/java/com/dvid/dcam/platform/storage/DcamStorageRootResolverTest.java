package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.dvid.dcam.feature.settings.domain.StorageMode;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

final class DcamStorageRootResolverTest {
    private static final long QUALIFIED = DcamStorageCapacityPolicy.MIN_START_FREE_BYTES;
    private final DcamStorageRootResolver resolver =
            new DcamStorageRootResolver(new DcamStorageCapacityPolicy());
    private final DcamStorageCandidate internal = candidate(StorageMode.INTERNAL, "internal", QUALIFIED);

    @Test void autoPrioritizesQualifiedExternalStorage() {
        DcamStorageResolution result = resolver.resolve(StorageMode.AUTO, internal,
                List.of(candidate(StorageMode.EXTERNAL, "sd-card", QUALIFIED)));

        assertEquals(StorageMode.EXTERNAL, result.getResolvedMode());
        assertEquals(new File("sd-card"), result.getRoot());
        assertFalse(result.isFallback());
    }

    @Test void fallsBackToInternalWhenExternalDoesNotQualify() {
        DcamStorageResolution result = resolver.resolve(StorageMode.AUTO, internal,
                List.of(candidate(StorageMode.EXTERNAL, "full-sd-card", QUALIFIED - 1L)));

        assertEquals(StorageMode.INTERNAL, result.getResolvedMode());
        assertEquals(new File("internal"), result.getRoot());
        assertTrue(result.isFallback());
    }

    @Test void explicitInternalNeverSelectsExternal() {
        DcamStorageResolution result = resolver.resolve(StorageMode.INTERNAL, internal,
                List.of(candidate(StorageMode.EXTERNAL, "sd-card", QUALIFIED)));

        assertEquals(StorageMode.INTERNAL, result.getResolvedMode());
        assertEquals(new File("internal"), result.getRoot());
        assertFalse(result.isFallback());
    }

    @Test void explicitExternalFallsBackOnlyWhenExternalDoesNotQualify() {
        DcamStorageResolution qualified = resolver.resolve(StorageMode.EXTERNAL, internal,
                List.of(candidate(StorageMode.EXTERNAL, "sd-card", QUALIFIED)));
        DcamStorageResolution unavailable = resolver.resolve(StorageMode.EXTERNAL, internal,
                List.of(new DcamStorageCandidate(StorageMode.EXTERNAL,
                        new File("sd-card"), false, false, 0L)));

        assertEquals(StorageMode.EXTERNAL, qualified.getResolvedMode());
        assertEquals(StorageMode.INTERNAL, unavailable.getResolvedMode());
        assertTrue(unavailable.isFallback());
    }

    @Test void nextCaptureCanFallBackAfterExternalLosesCapacity() {
        DcamStorageResolution firstCapture = resolver.resolve(StorageMode.AUTO, internal,
                List.of(candidate(StorageMode.EXTERNAL, "sd-card", QUALIFIED)));
        DcamStorageResolution nextCapture = resolver.resolve(StorageMode.AUTO, internal,
                List.of(candidate(StorageMode.EXTERNAL, "sd-card", QUALIFIED - 1L)));

        assertEquals(StorageMode.EXTERNAL, firstCapture.getResolvedMode());
        assertEquals(StorageMode.INTERNAL, nextCapture.getResolvedMode());
        assertTrue(nextCapture.isFallback());
    }

    private static DcamStorageCandidate candidate(StorageMode mode, String path, long available) {
        return new DcamStorageCandidate(mode, new File(path), true, true, available);
    }
}
