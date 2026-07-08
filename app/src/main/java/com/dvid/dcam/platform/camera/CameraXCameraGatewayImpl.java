package com.dvid.dcam.platform.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.core.logging.application.port.LogSink;
import com.dvid.dcam.feature.capture.application.port.CameraGateway;
import com.dvid.dcam.feature.capture.application.usecase.CaptureEventUseCase;
import com.dvid.dcam.feature.capture.domain.RecordingMode;
import com.dvid.dcam.feature.settings.application.usecase.MediaEncryptionSettingsUseCase;
import com.dvid.dcam.platform.recording.RecordingForegroundService;
import com.dvid.dcam.platform.storage.DcamFileType;
import com.dvid.dcam.platform.storage.DcamMediaFile;
import com.dvid.dcam.platform.storage.DcamMediaOutput;
import com.google.common.util.concurrent.ListenableFuture;
import java.time.LocalDateTime;

/** CameraX camera adapter. CameraX types do not escape through CameraGateway. */
public final class CameraXCameraGatewayImpl implements CameraGateway {
    private final Context context;
    private final DcamConfig config;
    private final DcamMediaOutput mediaOutput;
    private final LifecycleOwner lifecycleOwner;
    private final CameraXPreviewView previewView;
    private final LogSink log;
    private final CaptureEventUseCase captureEvents;
    private final MediaEncryptionSettingsUseCase mediaEncryptionSettings;
    private final ImageCapture imageCapture = new ImageCapture.Builder().build();
    private final VideoCapture<Recorder> videoCapture;
    private ListenableFuture<ProcessCameraProvider> providerFuture;
    private Recording activeRecording;
    private DcamFileType pendingRecordingType;

    public CameraXCameraGatewayImpl(Context context, LifecycleOwner lifecycleOwner, DcamConfig config,
                                    DcamMediaOutput mediaOutput, LogSink log,
                                    CaptureEventUseCase captureEvents,
                                    MediaEncryptionSettingsUseCase mediaEncryptionSettings,
                                    CameraXPreviewView previewView) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner; this.config = config; this.mediaOutput = mediaOutput; this.log = log;
        this.captureEvents = captureEvents;
        this.mediaEncryptionSettings = mediaEncryptionSettings;
        this.previewView = previewView;
        Recorder recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.FHD)).build();
        videoCapture = VideoCapture.withOutput(recorder);
        bindIfPermitted();
    }

    public void bindIfPermitted() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            previewView.showPermissionRequired(); return;
        }
        previewView.clearMessage();
        providerFuture = ProcessCameraProvider.getInstance(context);
        providerFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = providerFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.surfaceProvider());
                provider.unbindAll();
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, videoCapture);
            } catch (Exception error) { showError(error.getMessage()); }
        }, ContextCompat.getMainExecutor(context));
    }

    @Override public void takePhoto() {
        LocalDateTime at = LocalDateTime.now();
        boolean encrypt = mediaEncryptionSettings.isMediaEncryptionEnabled();
        DcamMediaFile mediaFile = mediaOutput.mediaFile(DcamFileType.IMAGE, config, at, encrypt);
        ImageCapture.OutputFileOptions options = mediaOutput.imageOptions(context, mediaFile);
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(ImageCapture.OutputFileResults result) {
                try {
                    if (encrypt) {
                        mediaOutput.encryptSaved(context, mediaFile, result.getSavedUri(),
                                config.getMediaEncryptionPassword());
                    }
                    mediaOutput.publishSaved(context, mediaFile);
                    previewView.showSaved(mediaFile.getFileName());
                    log.info((encrypt ? "Encrypted photo saved: " : "Photo saved: ") + mediaFile.getFileName());
                    captureEvents.photoSaved(mediaFile.getFileName());
                } catch (Exception error) {
                    log.error("Photo encryption failed: " + mediaFile.getFileName(), error);
                    captureEvents.captureFailed("Photo encryption", message(error));
                    previewView.showError("Photo encryption failed");
                }
            }
            @Override public void onError(ImageCaptureException error) {
                log.error("Photo failed", error);
                showError(error.getMessage());
            }
        });
    }

    @Override public void startVideo() {
        if (activeRecording == null) startRecording(DcamFileType.VIDEO);
    }

    @Override public void startSos() {
        startRecording(DcamFileType.SOS);
    }

    @Override public void stopRecording() {
        pendingRecordingType = null;
        if (activeRecording != null) activeRecording.stop();
    }

    private void startRecording(DcamFileType type) {
        if (activeRecording != null) {
            if (type == DcamFileType.SOS) {
                pendingRecordingType = DcamFileType.SOS;
                log.info("Stopping active recording before SOS handoff");
                activeRecording.stop();
            } else {
                log.warn("Ignored recording start while another recording is active", null);
            }
            return;
        }
        LocalDateTime at = LocalDateTime.now();
        boolean encrypt = mediaEncryptionSettings.isMediaEncryptionEnabled();
        DcamMediaFile mediaFile = mediaOutput.mediaFile(type, config, at, encrypt);
        PendingRecording pending = mediaOutput.prepareVideoRecording(context, videoCapture, mediaFile);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            pending = pending.withAudioEnabled();
        activeRecording = pending.start(ContextCompat.getMainExecutor(context), event -> {
            if (event instanceof VideoRecordEvent.Start) {
                previewView.showRecording(mediaFile.getFileName());
                log.info("Recording started: " + mediaFile.getFileName());
                if (!RecordingForegroundService.start(context, mediaFile.getFileName())) {
                    log.warn("Could not start recording foreground service", null);
                }
                RecordingMode mode = type == DcamFileType.SOS ? RecordingMode.SOS : RecordingMode.VIDEO;
                captureEvents.recordingStarted(mode, mediaFile.getFileName());
            }
            else if (event instanceof VideoRecordEvent.Finalize) {
                VideoRecordEvent.Finalize done = (VideoRecordEvent.Finalize) event;
                previewView.showFinalized(done.hasError()
                        ? "VIDEO ERROR " + done.getError() : "SAVED " + mediaFile.getFileName());
                if (done.hasError()) {
                    log.error("Recording failed: " + mediaFile.getFileName() + " error=" + done.getError(), null);
                    captureEvents.captureFailed("Recording", "CameraX error " + done.getError());
                }
                else {
                    try {
                        if (encrypt) {
                            mediaOutput.encryptSaved(context, mediaFile, done.getOutputResults().getOutputUri(),
                                    config.getMediaEncryptionPassword());
                        }
                        mediaOutput.publishSaved(context, mediaFile);
                        log.info((encrypt ? "Encrypted recording saved: " : "Recording saved: ")
                                + mediaFile.getFileName());
                        captureEvents.recordingCompleted(mediaFile.getFileName());
                    } catch (Exception error) {
                        log.error("Recording encryption failed: " + mediaFile.getFileName(), error);
                        captureEvents.captureFailed("Recording encryption", message(error));
                        previewView.showError("Recording encryption failed");
                    }
                }
                activeRecording = null;
                RecordingForegroundService.stop(context);
                if (pendingRecordingType != null) {
                    DcamFileType nextType = pendingRecordingType;
                    pendingRecordingType = null;
                    startRecording(nextType);
                }
            }
        });
    }

    private void showError(String text) {
        log.error("Camera error: " + text, null);
        captureEvents.captureFailed("Camera", text);
        previewView.showError(text);
    }

    private static String message(Exception error) {
        return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
    }

    public void release() {
        pendingRecordingType = null;
        if (activeRecording != null) activeRecording.stop();
        RecordingForegroundService.stop(context);
        if (providerFuture != null && providerFuture.isDone()) {
            try { providerFuture.get().unbindAll(); } catch (Exception ignored) {}
        }
    }
}
