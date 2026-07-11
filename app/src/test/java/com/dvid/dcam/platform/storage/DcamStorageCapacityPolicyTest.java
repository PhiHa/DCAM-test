package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

final class DcamStorageCapacityPolicyTest {
    private final DcamStorageCapacityPolicy policy = new DcamStorageCapacityPolicy();

    @Test void requiresEstimatedThirtyMinuteVideoPlusFinalizationReserve() {
        assertEquals(2_250_000_000L,
                DcamStorageCapacityPolicy.ESTIMATED_THIRTY_MINUTE_VIDEO_BYTES);
        assertEquals(500L * 1024L * 1024L,
                DcamStorageCapacityPolicy.RESERVED_FINALIZATION_BYTES);
        assertEquals(DcamStorageCapacityPolicy.ESTIMATED_THIRTY_MINUTE_VIDEO_BYTES
                        + DcamStorageCapacityPolicy.RESERVED_FINALIZATION_BYTES,
                DcamStorageCapacityPolicy.MIN_START_FREE_BYTES);
    }

    @Test void rejectsUnavailableUnwritableAndLowCapacityStorage() {
        assertFalse(policy.check(false, true, Long.MAX_VALUE).isReady());
        assertFalse(policy.check(true, false, Long.MAX_VALUE).isReady());
        assertFalse(policy.check(true, true,
                DcamStorageCapacityPolicy.MIN_START_FREE_BYTES - 1L).isReady());
        assertTrue(policy.check(true, true,
                DcamStorageCapacityPolicy.MIN_START_FREE_BYTES).isReady());
    }

    @Test void fileLimitPreservesFinalizationReserve() {
        long available = DcamStorageCapacityPolicy.MIN_START_FREE_BYTES;
        assertEquals(DcamStorageCapacityPolicy.ESTIMATED_THIRTY_MINUTE_VIDEO_BYTES,
                policy.recordingFileSizeLimit(available));
    }
}
