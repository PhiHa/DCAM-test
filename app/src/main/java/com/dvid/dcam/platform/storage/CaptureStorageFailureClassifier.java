package com.dvid.dcam.platform.storage;

import androidx.camera.video.VideoRecordEvent;

/** Maps CameraX terminal errors that mean the selected storage cannot continue safely. */
public final class CaptureStorageFailureClassifier {
    private CaptureStorageFailureClassifier() {}

    public static boolean isVideoStorageFailure(int errorCode) {
        return errorCode == VideoRecordEvent.Finalize.ERROR_INSUFFICIENT_STORAGE
                || errorCode == VideoRecordEvent.Finalize.ERROR_FILE_SIZE_LIMIT_REACHED;
    }
}
