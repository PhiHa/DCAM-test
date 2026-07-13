package com.dvid.dcam.platform.input;

import com.dvid.dcam.core.feature.application.usecase.FeatureGateSettingsUseCase;
import com.dvid.dcam.core.feature.domain.FeatureGate;
import com.dvid.dcam.core.input.domain.ButtonRole;
import com.dvid.dcam.core.input.domain.PhysicalButtonType;
import com.dvid.dcam.feature.auth.application.usecase.OperatorSessionUseCase;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCase;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCase;
import com.dvid.dcam.feature.capture.domain.RecordingMode;
import java.util.function.BiConsumer;

/** Applies Java button behavior policy to model-specific physical bindings. */
public final class HardwareButtonRouter {
    private static final long SOS_HOLD_MS = 3000L;
    private final PhotoCaptureUseCase photos;
    private final VideoRecordingUseCase videos;
    private final AudioRecordingUseCase audio;
    private final FeatureGateSettingsUseCase featureGates;
    private final OperatorSessionUseCase operatorSession;
    private final BiConsumer<Boolean, String> audioRecordingChanged;
    private final Runnable videoStopRequested;
    private volatile HardwareButtonLayout layout;
    private int heldButtonKeyCode = -1;
    private long holdStartedAtMs;
    private boolean holdHandled;

    public HardwareButtonRouter(
            PhotoCaptureUseCase photos,
            VideoRecordingUseCase videos,
            AudioRecordingUseCase audio,
            FeatureGateSettingsUseCase featureGates,
            OperatorSessionUseCase operatorSession,
            HardwareButtonLayout layout,
            BiConsumer<Boolean, String> audioRecordingChanged,
            Runnable videoStopRequested) {
        this.photos = photos;
        this.videos = videos;
        this.audio = audio;
        this.featureGates = featureGates;
        this.operatorSession = operatorSession;
        this.audioRecordingChanged = audioRecordingChanged == null
                ? (recording, fileName) -> {} : audioRecordingChanged;
        this.videoStopRequested = videoStopRequested == null ? () -> {} : videoStopRequested;
        if (layout == null) throw new IllegalArgumentException("layout is required");
        this.layout = layout;
    }

    public void updateLayout(HardwareButtonLayout layout) {
        if (layout == null) throw new IllegalArgumentException("layout is required");
        this.layout = layout;
        clearHoldState();
    }

    public boolean onKeyDown(int buttonKeyCode, int repeatCount, long eventTimeMs) {
        HardwareButtonBinding binding = layout.findByButtonKeyCode(buttonKeyCode);
        if (binding == null) return false;
        switch (binding.role()) {
            case RECORD:
                return handleRecordDown(binding.type(), repeatCount);
            case IMPORTANT_RECORDING:
                if (repeatCount == 0) {
                    notifyIfStoppingSos();
                    runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos::toggleSos);
                }
                return true;
            case PHOTO_CAPTURE:
                if (repeatCount == 0) {
                    runIfEnabled(FeatureGate.IMAGE_CAPTURE, photos::takePhoto);
                }
                return true;
            case AUDIO_CAPTURE:
                if (repeatCount == 0) {
                    runIfEnabled(FeatureGate.AUDIO_CAPTURE, () -> {
                        String fileName = audio.toggleAudio();
                        audioRecordingChanged.accept(audio.isAudioRecording(), fileName);
                    });
                }
                return true;
            case SOS:
                return handleSosDown(buttonKeyCode, repeatCount, eventTimeMs);
            case PTT:
            case POWER:
                return false;
            default:
                throw new IllegalArgumentException("Unsupported button role " + binding.role());
        }
    }

    public boolean onKeyUp(int buttonKeyCode) {
        HardwareButtonBinding binding = layout.findByButtonKeyCode(buttonKeyCode);
        if (binding == null) return false;
        if (binding.role() == ButtonRole.SOS) {
            if (heldButtonKeyCode == buttonKeyCode) clearHoldState();
            return true;
        }
        if (binding.role() == ButtonRole.RECORD
                && binding.type() == PhysicalButtonType.SWITCH) {
            if (videos.currentMode() != RecordingMode.IDLE) videoStopRequested.run();
            videos.stopRecording();
            return true;
        }
        return binding.role() != ButtonRole.PTT && binding.role() != ButtonRole.POWER;
    }

    private boolean handleRecordDown(PhysicalButtonType type, int repeatCount) {
        if (repeatCount != 0) return true;
        if (type == PhysicalButtonType.SWITCH) {
            runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos::startVideo);
        } else {
            if (videos.currentMode() != RecordingMode.IDLE) videoStopRequested.run();
            runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos::toggleVideo);
        }
        return true;
    }

    private boolean handleSosDown(int buttonKeyCode, int repeatCount, long eventTimeMs) {
        if (repeatCount == 0) {
            heldButtonKeyCode = buttonKeyCode;
            holdStartedAtMs = eventTimeMs;
            holdHandled = false;
        }
        if (!holdHandled && heldButtonKeyCode == buttonKeyCode
                && eventTimeMs - holdStartedAtMs >= SOS_HOLD_MS) {
            notifyIfStoppingSos();
            holdHandled = runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos::toggleSos);
        }
        return true;
    }

    private void clearHoldState() {
        heldButtonKeyCode = -1;
        holdStartedAtMs = 0L;
        holdHandled = false;
    }

    private void notifyIfStoppingSos() {
        if (videos.currentMode() == RecordingMode.SOS) videoStopRequested.run();
    }

    private boolean runIfEnabled(FeatureGate feature, Runnable action) {
        if (operatorSession != null && !operatorSession.hasActiveSession()) return false;
        if (featureGates != null) return featureGates.runIfEnabled(feature, action);
        action.run();
        return true;
    }
}
