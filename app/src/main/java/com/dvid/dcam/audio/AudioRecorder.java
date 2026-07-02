package com.dvid.dcam.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import androidx.core.content.ContextCompat;
import com.dvid.dcam.config.DcamConfig;
import com.dvid.dcam.logging.DcamLogger;
import com.dvid.dcam.storage.DcamFileName;
import com.dvid.dcam.storage.DcamFileType;
import com.dvid.dcam.storage.DcamMediaStore;
import com.dvid.dcam.storage.DcamStorage;
import java.io.File;
import java.time.LocalDateTime;

public final class AudioRecorder {
    private final Context context;
    private final DcamStorage storage;
    private MediaRecorder recorder;
    private File outputFile;
    private Uri outputUri;
    private ParcelFileDescriptor outputDescriptor;

    public AudioRecorder(Context context, DcamStorage storage) {
        this.context = context; this.storage = storage;
    }

    public String toggle(DcamConfig config) {
        if (recorder != null) {
            try { recorder.stop(); } finally { recorder.release(); recorder = null; closeDescriptor(); }
            String output = outputUri != null ? outputUri.toString() : outputFile == null ? null : outputFile.getAbsolutePath();
            DcamLogger.i("Audio saved: " + output);
            return output;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            DcamLogger.w("Audio permission missing", null);
            return null;
        }

        try {
            LocalDateTime at = LocalDateTime.now();
            String name = DcamFileName.build(DcamFileType.AUDIO, config.getAccountUserId(),
                    config.getPoliceUserId(), at, false);
            MediaRecorder next = Build.VERSION.SDK_INT >= 31 ? new MediaRecorder(context) : new MediaRecorder();
            next.setAudioSource(MediaRecorder.AudioSource.MIC);
            next.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            next.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            next.setAudioChannels(1);
            next.setAudioSamplingRate(8000);
            next.setAudioEncodingBitRate(64000);
            if (Build.VERSION.SDK_INT >= 29) {
                outputUri = DcamMediaStore.insertAudio(context.getContentResolver(), name, at);
                if (outputUri == null) { next.release(); return null; }
                outputDescriptor = context.getContentResolver().openFileDescriptor(outputUri, "w");
                if (outputDescriptor == null) { next.release(); return null; }
                next.setOutputFile(outputDescriptor.getFileDescriptor());
                outputFile = null;
            } else {
                outputFile = storage.outputFile(DcamFileType.AUDIO, config.getAccountUserId(),
                        config.getPoliceUserId(), at, false);
                next.setOutputFile(outputFile.getAbsolutePath());
                outputUri = null;
            }
            next.prepare();
            next.start();
            recorder = next;
            String output = outputUri != null ? outputUri.toString() : outputFile.getAbsolutePath();
            DcamLogger.i("Audio started: " + output);
            return output;
        } catch (Exception error) {
            if (recorder != null) recorder.release();
            recorder = null; closeDescriptor();
            DcamLogger.e("Audio failed", error);
            return null;
        }
    }

    public void release() {
        if (recorder != null) {
            try { recorder.stop(); } catch (RuntimeException ignored) {}
            recorder.release(); recorder = null;
        }
        closeDescriptor();
    }

    private void closeDescriptor() {
        if (outputDescriptor != null) {
            try { outputDescriptor.close(); } catch (Exception ignored) {}
            outputDescriptor = null;
        }
    }
}
