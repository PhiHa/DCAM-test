package com.dvid.dcam.platform.input;

import static com.dvid.dcam.core.input.domain.ButtonRole.AUDIO_CAPTURE;
import static com.dvid.dcam.core.input.domain.ButtonRole.IMPORTANT_RECORDING;
import static com.dvid.dcam.core.input.domain.ButtonRole.PHOTO_CAPTURE;
import static com.dvid.dcam.core.input.domain.ButtonRole.POWER;
import static com.dvid.dcam.core.input.domain.ButtonRole.PTT;
import static com.dvid.dcam.core.input.domain.ButtonRole.RECORD;
import static com.dvid.dcam.core.input.domain.ButtonRole.SOS;
import static com.dvid.dcam.core.input.domain.PhysicalButtonType.BUTTON;
import static com.dvid.dcam.core.input.domain.PhysicalButtonType.SWITCH;

import android.view.KeyEvent;
import com.dvid.dcam.core.input.domain.ButtonRole;
import com.dvid.dcam.core.input.domain.PhysicalButtonType;

/** Built-in device identity to hardware-button layout configuration. */
public final class HardwareButtonProfiles {
    private static final HardwareButtonLayout BWC = layout(
            button(PHOTO_CAPTURE, KeyEvent.KEYCODE_F2, BUTTON),
            button(PHOTO_CAPTURE, KeyEvent.KEYCODE_CAMERA, BUTTON),
            button(AUDIO_CAPTURE, KeyEvent.KEYCODE_F3, BUTTON),
            button(AUDIO_CAPTURE, KeyEvent.KEYCODE_VOLUME_DOWN, BUTTON),
            button(SOS, KeyEvent.KEYCODE_F7, BUTTON),
            button(RECORD, KeyEvent.KEYCODE_F10, SWITCH),
            button(RECORD, KeyEvent.KEYCODE_HEADSETHOOK, BUTTON));

    private static final HardwareButtonLayout BODY_CAMERA = layout(
            button(IMPORTANT_RECORDING, KeyEvent.KEYCODE_F1, BUTTON),
            button(PHOTO_CAPTURE, KeyEvent.KEYCODE_F2, BUTTON),
            button(AUDIO_CAPTURE, KeyEvent.KEYCODE_F3, BUTTON),
            button(PTT, KeyEvent.KEYCODE_F4, BUTTON),
            button(RECORD, KeyEvent.KEYCODE_F5, BUTTON),
            button(SOS, KeyEvent.KEYCODE_F7, BUTTON),
            button(POWER, KeyEvent.KEYCODE_POWER, BUTTON));

    private static final DeviceProfile[] PROFILES = {
            new DeviceProfile("BodyCamera", "k69v1_64_k419", "mt6768", BODY_CAMERA),
            new DeviceProfile("BWC", "k69v1_64_k419", "mt6768", BWC)
    };

    private HardwareButtonProfiles() {}

    public static HardwareButtonLayout resolve(HardwareDeviceIdentity identity) {
        if (identity == null) return HardwareButtonLayout.empty();
        for (DeviceProfile profile : PROFILES) {
            if (profile.matches(identity)) return profile.layout;
        }
        return HardwareButtonLayout.empty();
    }

    private static HardwareButtonBinding button(
            ButtonRole role, int buttonKeyCode, PhysicalButtonType type) {
        return new HardwareButtonBinding(role, buttonKeyCode, type);
    }

    private static HardwareButtonLayout layout(HardwareButtonBinding... bindings) {
        return new HardwareButtonLayout(bindings);
    }

    private static final class DeviceProfile {
        private final String productModel;
        private final String productDevice;
        private final String boardPlatform;
        private final HardwareButtonLayout layout;

        private DeviceProfile(
                String productModel,
                String productDevice,
                String boardPlatform,
                HardwareButtonLayout layout) {
            this.productModel = productModel;
            this.productDevice = productDevice;
            this.boardPlatform = boardPlatform;
            this.layout = layout;
        }

        private boolean matches(HardwareDeviceIdentity identity) {
            return productModel.equalsIgnoreCase(identity.productModel())
                    && productDevice.equalsIgnoreCase(identity.productDevice())
                    && boardPlatform.equalsIgnoreCase(identity.boardPlatform());
        }
    }
}
