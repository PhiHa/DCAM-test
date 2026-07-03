package com.dvid.dcam.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.dvid.dcam.domain.model.DcamConfig;
import com.dvid.dcam.domain.event.CaptureEventListener;
import com.dvid.dcam.domain.model.RecordingMode;
import com.dvid.dcam.domain.service.CameraService;
import com.dvid.dcam.domain.service.LogService;
import com.dvid.dcam.platform.recording.RecordingForegroundService;
import com.dvid.dcam.storage.DcamFileType;
import com.dvid.dcam.storage.DcamMediaFile;
import com.dvid.dcam.storage.DcamMediaOutput;
import com.google.common.util.concurrent.ListenableFuture;
import java.time.LocalDateTime;

/** CameraX platform adapter. CameraX types do not escape through CameraService. */
@SuppressLint("ViewConstructor")
public final class CameraPreview extends FrameLayout implements CameraService {
    private final DcamConfig config;
    private final DcamMediaOutput mediaOutput;
    private final LifecycleOwner lifecycleOwner;
    private final PreviewView previewView;
    private final LogService log;
    private final TextView message;
    private final ImageCapture imageCapture = new ImageCapture.Builder().build();
    private final VideoCapture<Recorder> videoCapture;
    private ListenableFuture<ProcessCameraProvider> providerFuture;
    private Recording activeRecording;
    private DcamFileType pendingRecordingType;
    private CaptureEventListener eventListener = CaptureEventListener.NONE;

    public CameraPreview(Context context, LifecycleOwner lifecycleOwner, DcamConfig config,
                         DcamMediaOutput mediaOutput, LogService log) {
        super(context);
        this.lifecycleOwner = lifecycleOwner; this.config = config; this.mediaOutput = mediaOutput; this.log = log;
        setBackgroundColor(Color.rgb(17, 17, 17));
        previewView = new PreviewView(context);
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
        addView(previewView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        message = new TextView(context);
        message.setTextColor(Color.WHITE); message.setGravity(Gravity.CENTER); message.setPadding(12, 12, 12, 12);
        LayoutParams messageParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        addView(message, messageParams);
        Recorder recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.FHD)).build();
        videoCapture = VideoCapture.withOutput(recorder);
        bindIfPermitted();
    }

    @Override public void setEventListener(CaptureEventListener listener) {
        eventListener = listener == null ? CaptureEventListener.NONE : listener;
    }

    @Override public void bindIfPermitted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            message.setText(com.dvid.dcam.R.string.camera_permission_required); return;
        }
        message.setText("");
        providerFuture = ProcessCameraProvider.getInstance(getContext());
        providerFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = providerFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                provider.unbindAll();
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, videoCapture);
            } catch (Exception error) { showError(error.getMessage()); }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    @Override public void takePhoto() {
        LocalDateTime at = LocalDateTime.now();
        DcamMediaFile mediaFile = mediaOutput.mediaFile(DcamFileType.IMAGE, config, at, false);
        ImageCapture.OutputFileOptions options = mediaOutput.imageOptions(getContext(), mediaFile);
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(ImageCapture.OutputFileResults result) {
                mediaOutput.publishSaved(getContext(), mediaFile);
                message.setText(getContext().getString(com.dvid.dcam.R.string.media_saved, mediaFile.getFileName()));
                log.info("Photo saved: " + mediaFile.getFileName());
                eventListener.onPhotoSaved(mediaFile.getFileName());
            }
            @Override public void onError(ImageCaptureException error) {
                log.error("Photo failed", error);
                showError(error.getMessage());
            }
        });
    }

    @Override public void toggleVideo() {
        if (activeRecording != null) stopRecording();
        else startVideo();
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
        DcamMediaFile mediaFile = mediaOutput.mediaFile(type, config, at, config.isVideoEncrypted());
        PendingRecording pending = mediaOutput.prepareVideoRecording(getContext(), videoCapture, mediaFile);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            pending = pending.withAudioEnabled();
        activeRecording = pending.start(ContextCompat.getMainExecutor(getContext()), event -> {
            if (event instanceof VideoRecordEvent.Start) {
                message.setText(getContext().getString(com.dvid.dcam.R.string.recording_file, mediaFile.getFileName()));
                log.info("Recording started: " + mediaFile.getFileName());
                if (!RecordingForegroundService.start(getContext(), mediaFile.getFileName())) {
                    log.warn("Could not start recording foreground service", null);
                }
                RecordingMode mode = type == DcamFileType.SOS ? RecordingMode.SOS : RecordingMode.VIDEO;
                eventListener.onRecordingStarted(mode, mediaFile.getFileName());
            }
            else if (event instanceof VideoRecordEvent.Finalize) {
                VideoRecordEvent.Finalize done = (VideoRecordEvent.Finalize) event;
                message.setText(done.hasError() ? "VIDEO ERROR " + done.getError() : "SAVED " + mediaFile.getFileName());
                if (done.hasError()) {
                    log.error("Recording failed: " + mediaFile.getFileName() + " error=" + done.getError(), null);
                    eventListener.onCaptureError("Recording", "CameraX error " + done.getError());
                }
                else {
                    mediaOutput.publishSaved(getContext(), mediaFile);
                    log.info("Recording saved: " + mediaFile.getFileName());
                    eventListener.onRecordingCompleted(mediaFile.getFileName());
                }
                activeRecording = null;
                RecordingForegroundService.stop(getContext());
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
        eventListener.onCaptureError("Camera", text);
        message.setTextColor(Color.RED);
        message.setText(text == null ? "Camera failed" : text);
    }

    @Override public void release() {
        pendingRecordingType = null;
        if (activeRecording != null) activeRecording.stop();
        RecordingForegroundService.stop(getContext());
        if (providerFuture != null && providerFuture.isDone()) {
            try { providerFuture.get().unbindAll(); } catch (Exception ignored) {}
        }
        eventListener = CaptureEventListener.NONE;
    }
}
