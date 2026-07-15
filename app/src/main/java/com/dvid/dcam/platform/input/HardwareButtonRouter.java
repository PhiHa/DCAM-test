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
    public static final long SOS_HOLD_MS = 3000L;
    private final PhotoCaptureUseCase photos;
    private final VideoRecordingUseCase videos;
    private final AudioRecordingUseCase audio;
    private final FeatureGateSettingsUseCase featureGates;
    private final OperatorSessionUseCase operatorSession;
    private final BiConsumer<Boolean, String> audioRecordingChanged;
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
            BiConsumer<Boolean, String> audioRecordingChanged) {
        this.photos = photos;
        this.videos = videos;
        this.audio = audio;
        this.featureGates = featureGates;
        this.operatorSession = operatorSession;
        this.audioRecordingChanged = audioRecordingChanged == null
                ? (recording, fileName) -> {} : audioRecordingChanged;
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
                    handleImportantRecording();
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
            stopVideoRecording();
            return true;
        }
        return binding.role() != ButtonRole.PTT;
    }

    public boolean isSosButton(int buttonKeyCode) {
        HardwareButtonBinding binding = layout.findByButtonKeyCode(buttonKeyCode);
        return binding != null && binding.role() == ButtonRole.SOS;
    }

    public boolean onSosHoldThreshold(int buttonKeyCode) {
        if (!holdHandled && heldButtonKeyCode == buttonKeyCode) {
            holdHandled = runIfEnabled(FeatureGate.VIDEO_CAPTURE, this::toggleSosRecording);
            return holdHandled;
        }
        return false;
    }

    private boolean handleRecordDown(PhysicalButtonType type, int repeatCount) {
        if (repeatCount != 0) return true;
        if (type == PhysicalButtonType.SWITCH) {
            runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos::startVideo);
        } else {
            runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos.currentMode() == RecordingMode.IDLE
                    ? videos::startVideo : this::stopVideoRecording);
        }
        return true;
    }

    private void handleImportantRecording() {
        runIfEnabled(FeatureGate.VIDEO_CAPTURE, videos.currentMode() == RecordingMode.IDLE
                ? videos::startSos : this::stopVideoRecording);
    }

    private boolean handleSosDown(int buttonKeyCode, int repeatCount, long eventTimeMs) {
        if (repeatCount == 0) {
            heldButtonKeyCode = buttonKeyCode;
            holdStartedAtMs = eventTimeMs;
            holdHandled = false;
        }
        if (!holdHandled && heldButtonKeyCode == buttonKeyCode
                && eventTimeMs - holdStartedAtMs >= SOS_HOLD_MS) {
            holdHandled = runIfEnabled(FeatureGate.VIDEO_CAPTURE, this::toggleSosRecording);
        }
        return true;
    }

    private void clearHoldState() {
        heldButtonKeyCode = -1;
        holdStartedAtMs = 0L;
        holdHandled = false;
    }

    private void toggleSosRecording() {
        if (videos.currentMode() == RecordingMode.IDLE) videos.startSos();
        else stopVideoRecording();
    }

    private void stopVideoRecording() {
        videos.stopRecording();
    }

    private boolean runIfEnabled(FeatureGate feature, Runnable action) {
        if (operatorSession != null && !operatorSession.hasActiveSession()) return false;
        if (featureGates != null) return featureGates.runIfEnabled(feature, action);
        action.run();
        return true;
    }
}
