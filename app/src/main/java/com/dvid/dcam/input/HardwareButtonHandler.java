package com.dvid.dcam.input;

import android.view.KeyEvent;
import com.dvid.dcam.app.DcamActions;

public final class HardwareButtonHandler {
    private HardwareButtonHandler() {}
    public static boolean onKeyDown(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: DcamActions.takePhoto.run(); return true;
            case KeyEvent.KEYCODE_HEADSETHOOK: DcamActions.toggleVideo.run(); return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN: DcamActions.toggleAudio.run(); return true;
            case KeyEvent.KEYCODE_F1:
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_F3: DcamActions.startSos.run(); return true;
            default: return false;
        }
    }
}
