package com.dvid.dcam.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.core.content.ContextCompat;
import com.dvid.dcam.domain.model.DcamConfig;
import com.dvid.dcam.domain.service.AudioService;
import com.dvid.dcam.domain.service.LogService;
import com.dvid.dcam.storage.DcamFileType;
import com.dvid.dcam.storage.DcamMediaFile;
import com.dvid.dcam.storage.DcamMediaOutput;
import java.io.File;
import java.time.LocalDateTime;

/** Android MediaRecorder platform adapter. */
public final class AudioRecorder implements AudioService {
    private final Context context;
    private final DcamMediaOutput mediaOutput;
    private final LogService log;
    private MediaRecorder recorder;
    private DcamMediaFile outputMediaFile;
    private File outputFile;

    public AudioRecorder(Context context, DcamMediaOutput mediaOutput, LogService log) {
        this.context = context; this.mediaOutput = mediaOutput; this.log = log;
    }

    @Override public String toggle(DcamConfig config) {
        if (recorder != null) {
            try { recorder.stop(); } finally { recorder.release(); recorder = null; }
            String output = outputFile == null ? null : outputFile.getAbsolutePath();
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
            String output = outputFile.getAbsolutePath();
            log.info("Audio started: " + output);
            return output;
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
