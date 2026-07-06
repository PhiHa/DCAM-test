package com.dvid.dcam.app;

import android.content.Context;
import androidx.activity.ComponentActivity;
import com.dvid.dcam.core.config.application.port.ConfigurationRepository;
import com.dvid.dcam.core.config.application.repository.ConfigurationRepositoryImpl;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.core.logging.application.port.LogSink;
import com.dvid.dcam.feature.capture.application.port.AudioRecorder;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCaseImpl;
import com.dvid.dcam.feature.capture.application.usecase.CaptureEventUseCase;
import com.dvid.dcam.feature.capture.application.usecase.CaptureEventUseCaseImpl;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCase;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCaseImpl;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCaseImpl;
import com.dvid.dcam.feature.device.application.port.DeviceRepository;
import com.dvid.dcam.feature.device.application.usecase.RefreshDeviceStatusUseCase;
import com.dvid.dcam.feature.device.application.usecase.RefreshDeviceStatusUseCaseImpl;
import com.dvid.dcam.feature.device.domain.DeviceInfo;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.application.port.MediaRepository;
import com.dvid.dcam.feature.media.application.usecase.BrowseMediaUseCase;
import com.dvid.dcam.feature.media.application.usecase.BrowseMediaUseCaseImpl;
import com.dvid.dcam.feature.media.application.usecase.OpenMediaUseCase;
import com.dvid.dcam.feature.media.application.usecase.OpenMediaUseCaseImpl;
import com.dvid.dcam.feature.settings.application.usecase.LanguageSettingsUseCase;
import com.dvid.dcam.feature.settings.application.usecase.LanguageSettingsUseCaseImpl;
import com.dvid.dcam.platform.audio.AndroidAudioRecorderImpl;
import com.dvid.dcam.platform.camera.CameraXCameraGatewayImpl;
import com.dvid.dcam.platform.config.AndroidLanguagePreferenceStoreImpl;
import com.dvid.dcam.platform.config.CsonConfigurationSourceImpl;
import com.dvid.dcam.platform.device.AndroidDeviceRepositoryImpl;
import com.dvid.dcam.platform.input.HardwareButtonRouter;
import com.dvid.dcam.platform.logging.DcamLogSinkImpl;
import com.dvid.dcam.platform.logging.DcamLogger;
import com.dvid.dcam.platform.storage.AndroidMediaOpenerImpl;
import com.dvid.dcam.platform.storage.DcamMediaOutput;
import com.dvid.dcam.platform.storage.DcamMediaOutputImpl;
import com.dvid.dcam.platform.storage.DcamStorage;
import com.dvid.dcam.platform.storage.LocalMediaRepositoryImpl;

/** Application composition root. This is the only place that selects concrete adapters. */
public final class AppComposition {
    private final DcamConfig config;
    private final DeviceStatus initialDeviceStatus;
    private final DcamStorage storage;
    private final DcamMediaOutput mediaOutput;
    private final LogSink logSink;
    private final RefreshDeviceStatusUseCase refreshDeviceStatus;
    private final BrowseMediaUseCase browseMedia;
    private final LanguageSettingsUseCase languageSettings;

    private AppComposition(Context context) {
        storage = DcamStorage.from(context);

        DeviceRepository deviceRepository = new AndroidDeviceRepositoryImpl(context);
        DeviceInfo deviceInfo = deviceRepository.readInfo();
        initialDeviceStatus = deviceRepository.readStatus();
        DcamLogger.init(context, deviceInfo);
        logSink = new DcamLogSinkImpl();

        ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl(
                new CsonConfigurationSourceImpl(storage), logSink);
        config = configurationRepository.load(deviceInfo.getHardwareId());
        DcamLogger.setCamId(config.getAccountUserId());

        refreshDeviceStatus = new RefreshDeviceStatusUseCaseImpl(deviceRepository);
        MediaRepository mediaRepository = new LocalMediaRepositoryImpl(storage);
        browseMedia = new BrowseMediaUseCaseImpl(mediaRepository);
        languageSettings = new LanguageSettingsUseCaseImpl(new AndroidLanguagePreferenceStoreImpl(context));
        mediaOutput = new DcamMediaOutputImpl(storage);
    }

    public static AppComposition create(Context context) {
        return new AppComposition(context.getApplicationContext());
    }

    public DcamConfig config() { return config; }
    public DeviceStatus initialDeviceStatus() { return initialDeviceStatus; }
    public RefreshDeviceStatusUseCase refreshDeviceStatusUseCase() { return refreshDeviceStatus; }
    public BrowseMediaUseCase browseMediaUseCase() { return browseMedia; }
    public LanguageSettingsUseCase languageSettingsUseCase() { return languageSettings; }

    public OpenMediaUseCase createOpenMediaUseCase(ComponentActivity owner) {
        return new OpenMediaUseCaseImpl(new AndroidMediaOpenerImpl(owner, storage, logSink));
    }

    public HardwareButtonRouter createHardwareButtonRouter(
            PhotoCaptureUseCase photos, VideoRecordingUseCase videos, AudioRecordingUseCase audio) {
        return new HardwareButtonRouter(photos, videos, audio);
    }

    public CaptureRuntime createCaptureRuntime(ComponentActivity owner) {
        CaptureEventUseCase captureEvents = new CaptureEventUseCaseImpl();
        AudioRecorder audioRecorder = new AndroidAudioRecorderImpl(owner, mediaOutput, logSink);
        CameraXCameraGatewayImpl camera = new CameraXCameraGatewayImpl(
                owner, owner, config, mediaOutput, logSink, captureEvents);
        PhotoCaptureUseCase photos = new PhotoCaptureUseCaseImpl(camera);
        VideoRecordingUseCase videos = new VideoRecordingUseCaseImpl(camera, captureEvents);
        AudioRecordingUseCase audio = new AudioRecordingUseCaseImpl(audioRecorder, config);
        return new CaptureRuntime(camera, audioRecorder, photos, videos, audio, captureEvents);
    }

    /** Lifecycle-bound Android capture adapters created for one Activity instance. */
    public static final class CaptureRuntime {
        private final CameraXCameraGatewayImpl camera;
        private final AudioRecorder audioRecorder;
        private final PhotoCaptureUseCase photos;
        private final VideoRecordingUseCase videos;
        private final AudioRecordingUseCase audio;
        private final CaptureEventUseCase captureEvents;

        private CaptureRuntime(
                CameraXCameraGatewayImpl camera,
                AudioRecorder audioRecorder,
                PhotoCaptureUseCase photos,
                VideoRecordingUseCase videos,
                AudioRecordingUseCase audio,
                CaptureEventUseCase captureEvents) {
            this.camera = camera;
            this.audioRecorder = audioRecorder;
            this.photos = photos;
            this.videos = videos;
            this.audio = audio;
            this.captureEvents = captureEvents;
        }

        public CameraXCameraGatewayImpl camera() { return camera; }
        public PhotoCaptureUseCase photoCapture() { return photos; }
        public VideoRecordingUseCase videoRecording() { return videos; }
        public AudioRecordingUseCase audioRecording() { return audio; }
        public CaptureEventUseCase captureEvents() { return captureEvents; }

        public void release() {
            camera.release();
            audioRecorder.release();
        }
    }
}
