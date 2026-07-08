package com.dvid.dcam.app.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.feature.capture.application.usecase.CaptureEventUseCase;
import com.dvid.dcam.feature.capture.domain.CaptureEvent;
import com.dvid.dcam.feature.capture.domain.CaptureState;
import com.dvid.dcam.feature.capture.domain.RecordingMode;
import com.dvid.dcam.feature.device.application.usecase.RefreshDeviceStatusUseCase;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.presentation.MediaBrowserState;
import com.dvid.dcam.feature.media.application.usecase.BrowseMediaUseCase;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainViewModel extends ViewModel {
    private final DcamConfig config;
    private final MutableLiveData<MainUiState> state;
    private CaptureEventUseCase captureEvents;
    private final RefreshDeviceStatusUseCase refreshDeviceStatus;
    private final BrowseMediaUseCase browseMedia;
    private final ExecutorService mediaIo = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "media-browser");
        thread.setDaemon(true);
        return thread;
    });

    public MainViewModel(
            DcamConfig config,
            DeviceStatus deviceStatus,
            RefreshDeviceStatusUseCase refreshDeviceStatus,
            BrowseMediaUseCase browseMedia) {
        this.config = config;
        this.refreshDeviceStatus = refreshDeviceStatus;
        this.browseMedia = browseMedia;
        state = new MutableLiveData<>(new MainUiState(
                MainScreen.CAMERA, new CaptureState(), deviceStatus, MediaBrowserState.root(), null));
    }

    public LiveData<MainUiState> state() { return state; }
    public DcamConfig getConfig() { return config; }

    public void bindCaptureEvents(CaptureEventUseCase events) {
        if (captureEvents == events) return;
        if (captureEvents != null) captureEvents.clearListener();
        captureEvents = events;
        if (captureEvents != null) captureEvents.setListener(this::onCaptureEvent);
    }

    public void unbindCaptureEvents(CaptureEventUseCase events) {
        if (captureEvents != events) return;
        captureEvents.clearListener();
        captureEvents = null;
    }

    public void onCapturePlatformReleased(CaptureEventUseCase events) {
        if (captureEvents == events && events.currentMode() != RecordingMode.IDLE) {
            onCaptureEvent(CaptureEvent.error("Recording", "camera lifecycle ended"));
        }
    }

    public void show(MainScreen screen) {
        MainUiState current = current();
        state.setValue(current.withScreen(screen));
        if (screen == MainScreen.FILES) openMediaFolder("");
    }

    public void openMediaFolder(String relativePath) {
        String path = relativePath == null ? "" : relativePath;
        state.setValue(current().withMediaBrowser(new MediaBrowserState(
                path, Collections.emptyList(), true, null)));
        mediaIo.execute(() -> {
            try {
                state.postValue(current().withMediaBrowser(
                        new MediaBrowserState(path, browseMedia.execute(path), false, null)));
            } catch (Exception error) {
                state.postValue(current().withMediaBrowser(
                        new MediaBrowserState(path, Collections.emptyList(), false, error.getMessage())));
            }
        });
    }

    public boolean navigateMediaUp() {
        String path = current().getMediaBrowser().getRelativePath();
        if (path.isEmpty()) return false;
        int slash = path.lastIndexOf('/');
        openMediaFolder(slash < 0 ? "" : path.substring(0, slash));
        return true;
    }

    public void refreshDeviceStatus() {
        state.setValue(current().withDeviceStatus(refreshDeviceStatus.execute()));
    }

    private void onCaptureEvent(CaptureEvent event) {
        switch (event.getType()) {
            case RECORDING_STARTED:
                state.setValue(current().withCapture(
                        new CaptureState(event.getMode(), event.getFileName(), System.currentTimeMillis()),
                        "Recording " + event.getFileName()));
                break;
            case RECORDING_COMPLETED:
                state.setValue(current().withCapture(new CaptureState(), "Saved " + event.getFileName()));
                break;
            case PHOTO_SAVED:
                state.setValue(current().withMessage("Saved " + event.getFileName()));
                break;
            case ERROR:
                String detail = event.getMessage() == null || event.getMessage().isBlank()
                        ? "unknown error" : event.getMessage();
                state.setValue(current().withCapture(
                        new CaptureState(), event.getOperation() + " failed: " + detail));
                break;
            default:
                throw new IllegalArgumentException("Unsupported capture event " + event.getType());
        }
    }

    private MainUiState current() {
        MainUiState current = state.getValue();
        return current == null
                ? new MainUiState(MainScreen.CAMERA, new CaptureState(), DeviceStatus.unknown(),
                        MediaBrowserState.root(), null)
                : current;
    }

    @Override protected void onCleared() {
        if (captureEvents != null) captureEvents.clearListener();
        captureEvents = null;
        mediaIo.shutdownNow();
        super.onCleared();
    }
}
