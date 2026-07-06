package com.dvid.dcam.app;

import android.content.Context;
import androidx.activity.ComponentActivity;
import com.dvid.dcam.core.config.ConfigurationRepository;
import com.dvid.dcam.core.config.DcamConfig;
import com.dvid.dcam.core.config.DefaultConfigurationRepository;
import com.dvid.dcam.core.logging.LogService;
import com.dvid.dcam.feature.capture.application.DcamCommandHandler;
import com.dvid.dcam.feature.capture.data.DefaultCaptureRepository;
import com.dvid.dcam.feature.capture.domain.CaptureRepository;
import com.dvid.dcam.feature.device.data.DefaultDeviceRepository;
import com.dvid.dcam.feature.device.domain.DeviceInfo;
import com.dvid.dcam.feature.device.domain.DeviceRepository;
import com.dvid.dcam.feature.device.domain.DeviceService;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.data.DefaultMediaRepository;
import com.dvid.dcam.feature.media.domain.MediaRepository;
import com.dvid.dcam.platform.audio.AudioRecorder;
import com.dvid.dcam.platform.camera.CameraPreview;
import com.dvid.dcam.platform.config.CsonConfigurationSource;
import com.dvid.dcam.platform.device.AndroidDeviceInfoProvider;
import com.dvid.dcam.platform.input.HardwareButtonRouter;
import com.dvid.dcam.platform.logging.DcamLogger;
import com.dvid.dcam.platform.logging.DcamLogService;
import com.dvid.dcam.platform.storage.DcamMediaOutput;
import com.dvid.dcam.platform.storage.DcamMediaOutputFactory;
import com.dvid.dcam.platform.storage.DcamStorage;
import com.dvid.dcam.platform.storage.LocalMediaBrowserService;

/** Application composition root. This is the only place that selects concrete adapters. */
public final class AppComposition {
    private final DcamConfig config;
    private final DeviceStatus initialDeviceStatus;
    private final DeviceRepository deviceRepository;
    private final MediaRepository mediaRepository;
    private final DcamMediaOutput mediaOutput;
    private final LogService logService;

    private AppComposition(Context context) {
        DeviceService deviceService = new AndroidDeviceInfoProvider(context);
        deviceRepository = new DefaultDeviceRepository(deviceService);
        DeviceInfo deviceInfo = deviceRepository.readInfo();
        initialDeviceStatus = deviceRepository.readStatus();
        DcamLogger.init(context, deviceInfo);
        logService = new DcamLogService();

        DcamStorage storage = DcamStorage.from(context);
        ConfigurationRepository configurationRepository = new DefaultConfigurationRepository(
                new CsonConfigurationSource(storage), logService);
        config = configurationRepository.load(deviceInfo.getHardwareId());
        DcamLogger.setCamId(config.getAccountUserId());

        mediaRepository = new DefaultMediaRepository(new LocalMediaBrowserService(storage));
        mediaOutput = new DcamMediaOutputFactory(storage);
    }

    public static AppComposition create(Context context) {
        return new AppComposition(context.getApplicationContext());
    }

    public DcamConfig config() { return config; }
    public DeviceStatus initialDeviceStatus() { return initialDeviceStatus; }
    public DeviceRepository deviceRepository() { return deviceRepository; }
    public MediaRepository mediaRepository() { return mediaRepository; }

    public HardwareButtonRouter createHardwareButtonRouter(DcamCommandHandler commands) {
        return new HardwareButtonRouter(commands);
    }

    public CaptureRuntime createCaptureRuntime(ComponentActivity owner) {
        AudioRecorder audio = new AudioRecorder(owner, mediaOutput, logService);
        CameraPreview camera = new CameraPreview(owner, owner, config, mediaOutput, logService);
        CaptureRepository repository = new DefaultCaptureRepository(camera, audio, config);
        return new CaptureRuntime(camera, audio, repository);
    }

    /** Lifecycle-bound Android capture adapters created for one Activity instance. */
    public static final class CaptureRuntime {
        private final CameraPreview camera;
        private final AudioRecorder audio;
        private final CaptureRepository repository;

        private CaptureRuntime(
                CameraPreview camera, AudioRecorder audio, CaptureRepository repository) {
            this.camera = camera;
            this.audio = audio;
            this.repository = repository;
        }

        public CameraPreview camera() { return camera; }
        public CaptureRepository repository() { return repository; }

        public void release() {
            camera.release();
            audio.release();
        }
    }
}
