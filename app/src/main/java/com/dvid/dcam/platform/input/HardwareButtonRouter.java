package com.dvid.dcam.platform.input;

import android.view.KeyEvent;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCase;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCase;
import com.dvid.dcam.feature.settings.application.usecase.FeatureGateSettingsUseCase;
import com.dvid.dcam.feature.settings.domain.FeatureGate;

/** Routes physical BodyCamera keys to application commands without global mutable actions. */
public final class HardwareButtonRouter {
    private static final long SOS_HOLD_MS = 3000L;
    private final PhotoCaptureUseCase photos;
    private final VideoRecordingUseCase videos;
    private final AudioRecordingUseCase audio;
    private final FeatureGateSettingsUseCase featureGates;
    private boolean sosHoldHandled;
    private long sosDownAtMs;

    public HardwareButtonRouter(
            PhotoCaptureUseCase photos, VideoRecordingUseCase videos, AudioRecordingUseCase audio) {
        this(photos, videos, audio, null);
    }

    public HardwareButtonRouter(
            PhotoCaptureUseCase photos,
            VideoRecordingUseCase videos,
            AudioRecordingUseCase audio,
            FeatureGateSettingsUseCase featureGates) {
        this.photos = photos;
        this.videos = videos;
        this.audio = audio;
        this.featureGates = featureGates;
    }

    public boolean onKeyDown(int keyCode, int repeatCount, long eventTimeMs) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_CAMERA:
                if (repeatCount == 0 && enabled(FeatureGate.IMAGE_CAPTURE)) photos.takePhoto();
                return true;
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (repeatCount == 0 && enabled(FeatureGate.AUDIO_CAPTURE)) audio.toggleAudio();
                return true;
            case KeyEvent.KEYCODE_F7:
                if (repeatCount == 0) { sosHoldHandled = false; sosDownAtMs = eventTimeMs; }
                if (enabled(FeatureGate.VIDEO_CAPTURE)
                        && !sosHoldHandled && eventTimeMs - sosDownAtMs >= SOS_HOLD_MS) {
                    videos.toggleSos();
                    sosHoldHandled = true;
                }
                return true;
            case KeyEvent.KEYCODE_F10:
                if (repeatCount == 0 && enabled(FeatureGate.VIDEO_CAPTURE)) videos.startVideo();
                return true;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (repeatCount == 0 && enabled(FeatureGate.VIDEO_CAPTURE)) videos.toggleVideo();
                return true;
            default:
                return false;
        }
    }

    public boolean onKeyUp(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_F7) {
            sosHoldHandled = false;
            sosDownAtMs = 0L;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_F10) {
            if (enabled(FeatureGate.VIDEO_CAPTURE)) videos.stopRecording();
            return true;
        }
        return false;
    }

    private boolean enabled(FeatureGate feature) {
        return featureGates == null || featureGates.isEnabled(feature);
    }
}
