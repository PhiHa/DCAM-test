package com.dvid.dcam.platform.input;

import android.os.Build;
import java.lang.reflect.Method;

/** Reads Android properties required by hardware-button profile matching. */
public final class AndroidHardwareDeviceIdentity {
    private AndroidHardwareDeviceIdentity() {}

    public static HardwareDeviceIdentity read() {
        return new HardwareDeviceIdentity(
                Build.MODEL,
                Build.DEVICE,
                systemProperty("ro.board.platform"));
    }

    private static String systemProperty(String name) {
        try {
            Class<?> properties = Class.forName("android.os.SystemProperties");
            Method get = properties.getMethod("get", String.class);
            Object value = get.invoke(null, name);
            return value instanceof String ? (String) value : "";
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return "";
        }
    }
}
