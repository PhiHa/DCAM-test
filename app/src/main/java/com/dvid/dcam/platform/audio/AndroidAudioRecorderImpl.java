package com.dvid.dcam.platform.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.core.content.ContextCompat;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.core.logging.application.port.LogSink;
import com.dvid.dcam.feature.capture.application.port.AudioRecorder;
import com.dvid.dcam.platform.storage.DcamFileType;
import com.dvid.dcam.platform.storage.DcamMediaFile;
import com.dvid.dcam.platform.storage.DcamMediaOutput;
import java.io.File;
import java.time.LocalDateTime;

/** Android MediaRecorder platform adapter. */
public final class AndroidAudioRecorderImpl implements AudioRecorder {
    private final Context context;
    private final DcamMediaOutput mediaOutput;
    private final LogSink log;
    private MediaRecorder recorder;
    private DcamMediaFile outputMediaFile;
    private File outputFile;

    public AndroidAudioRecorderImpl(Context context, DcamMediaOutput mediaOutput, LogSink log) {
        this.context = context; this.mediaOutput = mediaOutput; this.log = log;
    }

    @Override public String toggle(DcamConfig config) {
        if (recorder != null) {
            try { recorder.stop(); } finally { recorder.release(); recorder = null; }
            String output = outputMediaFile == null ? null : outputMediaFile.getFileName();
            if (outputMediaFile != null) mediaOutput.publishSaved(context, outputMediaFile);
            log.info("Audio saved: " + output);
            return output;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            log.warn("Audio permission missing", null);
            return null;
        }

        try {
            LocalDateTime at = LocalDateTime.now();
            outputMediaFile = mediaOutput.mediaFile(DcamFileType.AUDIO, config, at, false);
            MediaRecorder next = Build.VERSION.SDK_INT >= 31 ? new MediaRecorder(context) : new MediaRecorder();
            next.setAudioSource(MediaRecorder.AudioSource.MIC);
            next.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            next.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            next.setAudioChannels(1);
            next.setAudioSamplingRate(8000);
            next.setAudioEncodingBitRate(64000);
            outputFile = mediaOutput.audioFile(outputMediaFile);
            next.setOutputFile(outputFile.getAbsolutePath());
            next.prepare();
            next.start();
            recorder = next;
            log.info("Audio started: " + outputFile.getAbsolutePath());
            return outputMediaFile.getFileName();
        } catch (Exception error) {
            if (recorder != null) recorder.release();
            recorder = null; outputMediaFile = null; outputFile = null;
            log.error("Audio failed", error);
            return null;
        }
    }

    @Override public void release() {
        if (recorder != null) {
            try { recorder.stop(); } catch (RuntimeException ignored) {}
            recorder.release(); recorder = null;
        }
    }
}
