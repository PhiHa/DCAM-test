package com.dvid.dcam.platform.storage;

import android.content.Context;
import android.net.Uri;
import androidx.camera.core.ImageCapture;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public interface DcamMediaOutput {
    DcamMediaFile mediaFile(
            DcamFileType type,
            String cameraId,
            String fileUserId,
            LocalDateTime at,
            boolean encrypted);
    ImageCapture.OutputFileOptions imageOptions(Context context, DcamMediaFile mediaFile);
    PendingRecording prepareVideoRecording(Context context, VideoCapture<Recorder> videoCapture, DcamMediaFile mediaFile);
    File audioFile(DcamMediaFile mediaFile);
    void encryptSaved(Context context, DcamMediaFile mediaFile, Uri savedUri, String password) throws IOException;
    void publishSaved(Context context, DcamMediaFile mediaFile);
}
