package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import androidx.camera.video.VideoRecordEvent;
import org.junit.jupiter.api.Test;

final class CaptureStorageFailureClassifierTest {
    @Test void classifiesActiveRecordingStorageLimitFailures() {
        assertTrue(CaptureStorageFailureClassifier.isVideoStorageFailure(
                VideoRecordEvent.Finalize.ERROR_INSUFFICIENT_STORAGE));
        assertTrue(CaptureStorageFailureClassifier.isVideoStorageFailure(
                VideoRecordEvent.Finalize.ERROR_FILE_SIZE_LIMIT_REACHED));
        assertFalse(CaptureStorageFailureClassifier.isVideoStorageFailure(
                VideoRecordEvent.Finalize.ERROR_ENCODING_FAILED));
    }
}
