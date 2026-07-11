package com.dvid.dcam.platform.storage;

/** Build 0.1 free-space policy for image and video capture. */
public final class DcamStorageCapacityPolicy {
    public static final long ESTIMATED_VIDEO_BITS_PER_SECOND = 10_000_000L;
    public static final long ESTIMATED_THIRTY_MINUTE_VIDEO_BYTES =
            ESTIMATED_VIDEO_BITS_PER_SECOND * 30L * 60L / 8L;
    public static final long RESERVED_FINALIZATION_BYTES = 500L * 1024L * 1024L;
    public static final long MIN_START_FREE_BYTES =
            ESTIMATED_THIRTY_MINUTE_VIDEO_BYTES + RESERVED_FINALIZATION_BYTES;

    public CaptureStorageCheck check(boolean mounted, boolean writable, long availableBytes) {
        if (!mounted) {
            return CaptureStorageCheck.rejected(
                    availableBytes, MIN_START_FREE_BYTES, "Storage is unavailable");
        }
        if (!writable) {
            return CaptureStorageCheck.rejected(
                    availableBytes, MIN_START_FREE_BYTES, "Storage is not writable");
        }
        if (availableBytes < MIN_START_FREE_BYTES) {
            return CaptureStorageCheck.rejected(
                    availableBytes, MIN_START_FREE_BYTES, "Not enough free storage");
        }
        return CaptureStorageCheck.ready(availableBytes, MIN_START_FREE_BYTES);
    }

    /** Maximum bytes CameraX may consume while preserving finalization space. */
    public long recordingFileSizeLimit(long availableBytes) {
        return Math.max(1L, availableBytes - RESERVED_FINALIZATION_BYTES);
    }
}
