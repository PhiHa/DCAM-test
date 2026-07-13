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
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;

public final class DcamMediaOutputImpl implements DcamMediaOutput {
    private static final ExecutorService FINALIZATION_EXECUTOR =
            Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "dcam-media-finalization");
                thread.setDaemon(true);
                return thread;
            });
    private final DcamStorage storage;
    private final DcamMediaFinalizer finalizer;
    private final ExecutorService finalizationExecutor;
    private final BooleanSupplier createVideoMd5;

    public DcamMediaOutputImpl(DcamStorage storage) {
        this(storage, null);
    }

    public DcamMediaOutputImpl(DcamStorage storage, BooleanSupplier createVideoMd5) {
        this.storage = storage;
        this.finalizer = new DcamMediaFinalizer(storage);
        this.finalizationExecutor = FINALIZATION_EXECUTOR;
        this.createVideoMd5 = createVideoMd5;
    }

    @Override public DcamMediaFile mediaFile(
            DcamFileType type,
            String cameraId,
            String fileUserId,
            LocalDateTime at,
            boolean encrypted) {
        return storage.mediaFile(type, cameraId, fileUserId, at, encrypted);
    }

    @Override public CaptureStorageCheck checkCaptureReady() {
        return storage.checkCaptureReady();
    }

    @Override public long recordingFileSizeLimit() {
        return storage.recordingFileSizeLimit();
    }

    @Override public ImageCapture.OutputFileOptions imageOptions(Context context, DcamMediaFile mediaFile) {
        if (usesPublicMediaStore(mediaFile.getType())) {
            return new ImageCapture.OutputFileOptions.Builder(context.getContentResolver(),
                    DcamMediaStore.imageCollection(), DcamMediaStore.values(mediaFile)).build();
        }
        return new ImageCapture.OutputFileOptions.Builder(storage.prepareFile(mediaFile)).build();
    }

    @Override public PendingRecording prepareVideoRecording(
            Context context,
            VideoCapture<Recorder> videoCapture,
            DcamMediaFile mediaFile,
            long fileSizeLimitBytes) {
        if (usesPublicMediaStore(mediaFile.getType())) {
            MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(context.getContentResolver(),
                    DcamMediaStore.videoCollection())
                    .setContentValues(DcamMediaStore.values(mediaFile))
                    .setFileSizeLimit(fileSizeLimitBytes)
                    .build();
            return videoCapture.getOutput().prepareRecording(context, options);
        }
        FileOutputOptions options = new FileOutputOptions.Builder(storage.prepareFile(mediaFile))
                .setFileSizeLimit(fileSizeLimitBytes)
                .build();
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

    @Override public void finalizeSaved(
            Context context, DcamMediaFile mediaFile, FinalizationCallback callback) {
        finalizationExecutor.execute(() -> {
            try {
                boolean createMd5 = createVideoMd5 != null && createVideoMd5.getAsBoolean();
                File finalFile = finalizer.finalizeMedia(mediaFile, createMd5);
                if (storage.isPublicDcim()) {
                    MediaScannerConnection.scanFile(context,
                            new String[] { finalFile.getAbsolutePath() },
                            new String[] { mediaFile.getType().getMimeType() }, null);
                }
                callback.onSuccess(finalFile);
            } catch (Exception failure) {
                callback.onFailure(failure);
            }
        });
    }

    @Override public void recoverStaged(RecoveryCallback callback) {
        finalizationExecutor.execute(() -> callback.onComplete(
                new DcamStagedMediaRecovery(
                        storage, finalizer, new AndroidDcamMediaValidator(), createVideoMd5).recover()));
    }

    private boolean usesPublicMediaStore(DcamFileType type) {
        return Build.VERSION.SDK_INT >= 29 && storage.isPublicDcim() && type != DcamFileType.AUDIO;
    }
}
