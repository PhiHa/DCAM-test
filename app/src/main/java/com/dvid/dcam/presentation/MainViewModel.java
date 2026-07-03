package com.dvid.dcam.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.dvid.dcam.application.command.DcamCommandHandler;
import com.dvid.dcam.application.usecase.StartSosUseCase;
import com.dvid.dcam.application.usecase.BrowseMediaUseCase;
import com.dvid.dcam.application.usecase.RefreshDeviceStatusUseCase;
import com.dvid.dcam.application.usecase.StartVideoUseCase;
import com.dvid.dcam.application.usecase.StopRecordingUseCase;
import com.dvid.dcam.application.usecase.TakePhotoUseCase;
import com.dvid.dcam.application.usecase.ToggleAudioUseCase;
import com.dvid.dcam.application.usecase.ToggleVideoUseCase;
import com.dvid.dcam.domain.model.DcamConfig;
import com.dvid.dcam.domain.model.DeviceStatus;
import com.dvid.dcam.domain.event.CaptureEventListener;
import com.dvid.dcam.domain.model.CaptureState;
import com.dvid.dcam.domain.model.RecordingMode;
import com.dvid.dcam.domain.repository.CaptureRepository;
import com.dvid.dcam.domain.repository.DeviceRepository;
import com.dvid.dcam.domain.repository.MediaRepository;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainViewModel extends ViewModel implements DcamCommandHandler, CaptureEventListener {
    private final DcamConfig config;
    private final MutableLiveData<MainUiState> state;
    private CaptureRepository repository;
    private final RefreshDeviceStatusUseCase refreshDeviceStatus;
    private final BrowseMediaUseCase browseMedia;
    private final ExecutorService mediaIo = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "media-browser");
        thread.setDaemon(true);
        return thread;
    });
    private TakePhotoUseCase takePhoto;
    private ToggleVideoUseCase toggleVideo;
    private StartVideoUseCase startVideo;
    private StopRecordingUseCase stopRecording;
    private StartSosUseCase startSos;
    private ToggleAudioUseCase toggleAudio;

    public MainViewModel(DcamConfig config, DeviceStatus deviceStatus, DeviceRepository deviceRepository,
                         MediaRepository mediaRepository) {
        this.config = config;
        refreshDeviceStatus = new RefreshDeviceStatusUseCase(deviceRepository);
        browseMedia = new BrowseMediaUseCase(mediaRepository);
        state = new MutableLiveData<>(new MainUiState(
                MainScreen.CAMERA, new CaptureState(), deviceStatus, MediaBrowserState.root(), null));
    }

    public LiveData<MainUiState> state() { return state; }
    public DcamConfig getConfig() { return config; }

    public void attach(CaptureRepository nextRepository) {
        if (repository != null) repository.setEventListener(CaptureEventListener.NONE);
        repository = nextRepository;
        repository.setEventListener(this);
        takePhoto = new TakePhotoUseCase(repository);
        toggleVideo = new ToggleVideoUseCase(repository);
        startVideo = new StartVideoUseCase(repository);
        stopRecording = new StopRecordingUseCase(repository);
        startSos = new StartSosUseCase(repository);
        toggleAudio = new ToggleAudioUseCase(repository);
    }

    public void detach(CaptureRepository expected) {
        if (repository != expected) return;
        repository.setEventListener(CaptureEventListener.NONE);
        repository = null;
        takePhoto = null;
        toggleVideo = null;
        startVideo = null;
        stopRecording = null;
        startSos = null;
        toggleAudio = null;
    }

    public void onCapturePlatformReleased() {
        if (current().getCapture().getMode() != RecordingMode.IDLE) {
            state.setValue(current().withCapture(new CaptureState(), "Recording stopped: camera lifecycle ended"));
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

    @Override public void takePhoto() { if (takePhoto != null) takePhoto.execute(); }
    @Override public void toggleVideo() { if (toggleVideo != null) toggleVideo.execute(); }
    @Override public void startVideo() { if (startVideo != null) startVideo.execute(); }
    @Override public void stopRecording() { if (stopRecording != null) stopRecording.execute(); }

    @Override public void toggleAudio() {
        if (toggleAudio == null) return;
        String output = toggleAudio.execute();
        state.setValue(current().withMessage(output == null ? "Audio unavailable" : "Audio " + output));
    }

    @Override public void toggleSos() {
        RecordingMode mode = current().getCapture().getMode();
        if (mode == RecordingMode.SOS) {
            stopRecording();
        } else if (startSos != null) {
            startSos.execute();
        }
    }

    @Override public void onRecordingStarted(RecordingMode mode, String fileName) {
        CaptureState capture = new CaptureState(mode, fileName, System.currentTimeMillis());
        state.setValue(current().withCapture(capture, "Recording " + fileName));
    }

    @Override public void onRecordingCompleted(String fileName) {
        state.setValue(current().withCapture(new CaptureState(), "Saved " + fileName));
    }

    @Override public void onPhotoSaved(String fileName) {
        state.setValue(current().withMessage("Saved " + fileName));
    }

    @Override public void onCaptureError(String operation, String message) {
        String detail = message == null || message.isBlank() ? "unknown error" : message;
        state.setValue(current().withCapture(new CaptureState(), operation + " failed: " + detail));
    }

    private MainUiState current() {
        MainUiState current = state.getValue();
        return current == null
                ? new MainUiState(MainScreen.CAMERA, new CaptureState(), DeviceStatus.unknown(),
                        MediaBrowserState.root(), null)
                : current;
    }

    @Override protected void onCleared() {
        mediaIo.shutdownNow();
        super.onCleared();
    }
}
