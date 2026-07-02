package com.dvid.dcam.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.MediaStoreOutputOptions;
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
import com.dvid.dcam.config.DcamConfig;
import com.dvid.dcam.logging.DcamLogger;
import com.dvid.dcam.storage.DcamFileName;
import com.dvid.dcam.storage.DcamFileType;
import com.dvid.dcam.storage.DcamMediaStore;
import com.dvid.dcam.storage.DcamStorage;
import com.google.common.util.concurrent.ListenableFuture;
import java.time.LocalDateTime;

public final class CameraPreview extends FrameLayout {
    private final DcamConfig config;
    private final LifecycleOwner lifecycleOwner;
    private final PreviewView previewView;
    private final TextView message;
    private final ImageCapture imageCapture = new ImageCapture.Builder().build();
    private final VideoCapture<Recorder> videoCapture;
    private ListenableFuture<ProcessCameraProvider> providerFuture;
    private Recording activeRecording;

    public CameraPreview(Context context, LifecycleOwner lifecycleOwner, DcamConfig config) {
        super(context);
        this.lifecycleOwner = lifecycleOwner; this.config = config;
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
        CameraActions.takePhoto = this::takePhoto;
        VideoActions.toggleVideo = this::toggleVideo;
        VideoActions.startVideo = this::startVideo;
        VideoActions.stopVideo = this::stopVideo;
        VideoActions.startSos = () -> startRecording(DcamFileType.SOS);
        bindIfPermitted();
    }

    public void bindIfPermitted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            message.setText("CAMERA PERMISSION REQUIRED"); return;
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

    private void takePhoto() {
        LocalDateTime at = LocalDateTime.now();
        String name = DcamFileName.build(DcamFileType.IMAGE, config.getAccountUserId(), config.getPoliceUserId(), at, false);
        ImageCapture.OutputFileOptions options;
        if (Build.VERSION.SDK_INT >= 29) {
            options = new ImageCapture.OutputFileOptions.Builder(getContext().getContentResolver(),
                    DcamMediaStore.imageCollection(), DcamMediaStore.values(DcamFileType.IMAGE, name, at)).build();
        } else {
            options = new ImageCapture.OutputFileOptions.Builder(new DcamStorage().outputFile(DcamFileType.IMAGE,
                    config.getAccountUserId(), config.getPoliceUserId(), at, false)).build();
        }
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(ImageCapture.OutputFileResults result) { message.setText("SAVED " + name); DcamLogger.i("Photo saved: " + name); }
            @Override public void onError(ImageCaptureException error) { DcamLogger.e("Photo failed", error); showError(error.getMessage()); }
        });
    }

    private void toggleVideo() {
        if (activeRecording != null) stopVideo();
        else startVideo();
    }

    private void startVideo() {
        if (activeRecording == null) startRecording(DcamFileType.VIDEO);
    }

    private void stopVideo() {
        if (activeRecording != null) activeRecording.stop();
    }

    private void startRecording(DcamFileType type) {
        if (activeRecording != null) { activeRecording.stop(); activeRecording = null; }
        LocalDateTime at = LocalDateTime.now();
        String name = DcamFileName.build(type, config.getAccountUserId(), config.getPoliceUserId(), at, config.isVideoEncrypted());
        PendingRecording pending;
        if (Build.VERSION.SDK_INT >= 29) {
            MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(getContext().getContentResolver(),
                    DcamMediaStore.videoCollection()).setContentValues(DcamMediaStore.values(type, name, at)).build();
            pending = videoCapture.getOutput().prepareRecording(getContext(), options);
        } else {
            FileOutputOptions options = new FileOutputOptions.Builder(new DcamStorage().outputFile(type,
                    config.getAccountUserId(), config.getPoliceUserId(), at, config.isVideoEncrypted())).build();
            pending = videoCapture.getOutput().prepareRecording(getContext(), options);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            pending = pending.withAudioEnabled();
        activeRecording = pending.start(ContextCompat.getMainExecutor(getContext()), event -> {
            if (event instanceof VideoRecordEvent.Start) { message.setText("REC " + name); DcamLogger.i("Recording started: " + name); }
            else if (event instanceof VideoRecordEvent.Finalize) {
                VideoRecordEvent.Finalize done = (VideoRecordEvent.Finalize) event;
                message.setText(done.hasError() ? "VIDEO ERROR " + done.getError() : "SAVED " + name);
                if (done.hasError()) DcamLogger.e("Recording failed: " + name + " error=" + done.getError(), null);
                else DcamLogger.i("Recording saved: " + name);
                activeRecording = null;
            }
        });
    }

    private void showError(String text) { DcamLogger.e("Camera error: " + text, null); message.setTextColor(Color.RED); message.setText(text == null ? "Camera failed" : text); }

    public void release() {
        if (activeRecording != null) activeRecording.stop();
        if (providerFuture != null && providerFuture.isDone()) {
            try { providerFuture.get().unbindAll(); } catch (Exception ignored) {}
        }
        CameraActions.takePhoto = () -> {}; VideoActions.toggleVideo = () -> {}; VideoActions.startVideo = () -> {}; VideoActions.stopVideo = () -> {}; VideoActions.startSos = () -> {};
    }
}
