package com.dvid.dcam.platform.storage;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import androidx.camera.core.ImageCapture;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import com.dvid.dcam.core.config.domain.DcamConfig;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public final class DcamMediaOutputImpl implements DcamMediaOutput {
    private final DcamStorage storage;

    public DcamMediaOutputImpl(DcamStorage storage) { this.storage = storage; }

    @Override public DcamMediaFile mediaFile(DcamFileType type, DcamConfig config, LocalDateTime at, boolean encrypted) {
        return storage.mediaFile(type, config.getAccountUserId(), config.getPoliceUserId(), at, encrypted);
    }

    @Override public ImageCapture.OutputFileOptions imageOptions(Context context, DcamMediaFile mediaFile) {
        if (usesPublicMediaStore(mediaFile.getType())) {
            return new ImageCapture.OutputFileOptions.Builder(context.getContentResolver(),
                    DcamMediaStore.imageCollection(), DcamMediaStore.values(mediaFile)).build();
        }
        return new ImageCapture.OutputFileOptions.Builder(storage.prepareFile(mediaFile)).build();
    }

    @Override public PendingRecording prepareVideoRecording(Context context, VideoCapture<Recorder> videoCapture,
                                                  DcamMediaFile mediaFile) {
        if (usesPublicMediaStore(mediaFile.getType())) {
            MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(context.getContentResolver(),
                    DcamMediaStore.videoCollection()).setContentValues(DcamMediaStore.values(mediaFile)).build();
            return videoCapture.getOutput().prepareRecording(context, options);
        }
        FileOutputOptions options = new FileOutputOptions.Builder(storage.prepareFile(mediaFile)).build();
        return videoCapture.getOutput().prepareRecording(context, options);
    }

    @Override public File audioFile(DcamMediaFile mediaFile) {
        return storage.prepareFile(mediaFile);
    }

    @Override public void encryptSaved(Context context, DcamMediaFile mediaFile, Uri savedUri, String password)
            throws IOException {
        if (savedUri != null) {
            BodycamMediaCrypto.encryptContentUri(
                    context.getContentResolver(), savedUri, context.getCacheDir(), password);
            return;
        }
        BodycamMediaCrypto.encryptFileInPlace(mediaFile.getFile(), password);
    }

    @Override public void publishSaved(Context context, DcamMediaFile mediaFile) {
        if (!storage.isPublicDcim() || usesPublicMediaStore(mediaFile.getType())) return;
        MediaScannerConnection.scanFile(context, new String[] { mediaFile.getFile().getAbsolutePath() },
                new String[] { mediaFile.getType().getMimeType() }, null);
    }

    private boolean usesPublicMediaStore(DcamFileType type) {
        return Build.VERSION.SDK_INT >= 29 && storage.isPublicDcim() && type != DcamFileType.AUDIO;
    }
}
