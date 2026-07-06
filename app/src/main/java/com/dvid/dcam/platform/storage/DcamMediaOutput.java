package com.dvid.dcam.platform.storage;

import android.content.Context;
import androidx.camera.core.ImageCapture;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import com.dvid.dcam.core.config.domain.DcamConfig;
import java.io.File;
import java.time.LocalDateTime;

public interface DcamMediaOutput {
    DcamMediaFile mediaFile(DcamFileType type, DcamConfig config, LocalDateTime at, boolean encrypted);
    ImageCapture.OutputFileOptions imageOptions(Context context, DcamMediaFile mediaFile);
    PendingRecording prepareVideoRecording(Context context, VideoCapture<Recorder> videoCapture, DcamMediaFile mediaFile);
    File audioFile(DcamMediaFile mediaFile);
    void publishSaved(Context context, DcamMediaFile mediaFile);
}
