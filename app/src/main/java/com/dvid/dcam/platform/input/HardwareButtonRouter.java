package com.dvid.dcam.platform.input;

import android.view.KeyEvent;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCase;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCase;

/** Routes physical BodyCamera keys to application commands without global mutable actions. */
public final class HardwareButtonRouter {
    private static final long SOS_HOLD_MS = 3000L;
    private final PhotoCaptureUseCase photos;
    private final VideoRecordingUseCase videos;
    private final AudioRecordingUseCase audio;
    private boolean sosHoldHandled;
    private long sosDownAtMs;

    public HardwareButtonRouter(
            PhotoCaptureUseCase photos, VideoRecordingUseCase videos, AudioRecordingUseCase audio) {
        this.photos = photos;
        this.videos = videos;
        this.audio = audio;
    }

    public boolean onKeyDown(int keyCode, int repeatCount, long eventTimeMs) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_CAMERA:
                if (repeatCount == 0) photos.takePhoto();
                return true;
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (repeatCount == 0) audio.toggleAudio();
                return true;
            case KeyEvent.KEYCODE_F7:
                if (repeatCount == 0) { sosHoldHandled = false; sosDownAtMs = eventTimeMs; }
                if (!sosHoldHandled && eventTimeMs - sosDownAtMs >= SOS_HOLD_MS) {
                    videos.toggleSos();
                    sosHoldHandled = true;
                }
                return true;
            case KeyEvent.KEYCODE_F10:
                if (repeatCount == 0) videos.startVideo();
                return true;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (repeatCount == 0) videos.toggleVideo();
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
            videos.stopRecording();
            return true;
        }
        return false;
    }
}
