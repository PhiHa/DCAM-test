package com.dvid.dcam.input;

import android.view.KeyEvent;
import com.dvid.dcam.application.command.DcamCommandHandler;

/** Routes physical BodyCamera keys to application commands without global mutable actions. */
public final class HardwareButtonRouter {
    private static final long SOS_HOLD_MS = 3000L;
    private final DcamCommandHandler commands;
    private boolean sosHoldHandled;
    private long sosDownAtMs;

    public HardwareButtonRouter(DcamCommandHandler commands) {
        this.commands = commands;
    }

    public boolean onKeyDown(int keyCode, int repeatCount, long eventTimeMs) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_CAMERA:
                if (repeatCount == 0) commands.takePhoto();
                return true;
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (repeatCount == 0) commands.toggleAudio();
                return true;
            case KeyEvent.KEYCODE_F7:
                if (repeatCount == 0) { sosHoldHandled = false; sosDownAtMs = eventTimeMs; }
                if (!sosHoldHandled && eventTimeMs - sosDownAtMs >= SOS_HOLD_MS) {
                    commands.toggleSos();
                    sosHoldHandled = true;
                }
                return true;
            case KeyEvent.KEYCODE_F10:
                if (repeatCount == 0) commands.startVideo();
                return true;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (repeatCount == 0) commands.toggleVideo();
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
            commands.stopRecording();
            return true;
        }
        return false;
    }
}
