package com.dvid.dcam.input;

import android.view.KeyEvent;
import com.dvid.dcam.app.DcamActions;

public final class HardwareButtonHandler {
    private static final long SOS_HOLD_MS = 3000L;
    private static boolean sosHoldHandled;
    private static long sosDownAtMs;

    private HardwareButtonHandler() {}

    public static boolean onKeyDown(int keyCode, int repeatCount, long eventTimeMs) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F2:
                if (repeatCount == 0) DcamActions.takePhoto.run(); return true;
            case KeyEvent.KEYCODE_F3:
                if (repeatCount == 0) DcamActions.toggleAudio.run(); return true;
            case KeyEvent.KEYCODE_F7:
                if (repeatCount == 0) { sosHoldHandled = false; sosDownAtMs = eventTimeMs; }
                if (!sosHoldHandled && eventTimeMs - sosDownAtMs >= SOS_HOLD_MS) {
                    DcamActions.toggleSos.run(); sosHoldHandled = true;
                }
                return true;
            case KeyEvent.KEYCODE_F10:
                if (repeatCount == 0) DcamActions.startVideo.run(); return true;
            case KeyEvent.KEYCODE_CAMERA:
                if (repeatCount == 0) DcamActions.takePhoto.run(); return true;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (repeatCount == 0) DcamActions.toggleVideo.run(); return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (repeatCount == 0) DcamActions.toggleAudio.run(); return true;
            default:
                return false;
        }
    }

    public static boolean onKeyUp(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_F7) {
            sosHoldHandled = false; sosDownAtMs = 0L; return true;
        }
        if (keyCode == KeyEvent.KEYCODE_F10) {
            DcamActions.stopVideo.run(); return true;
        }
        return false;
    }
}
