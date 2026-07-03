package com.dvid.dcam.device;

import android.content.Context;
import android.provider.Settings;

public final class AndroidDeviceInfoProvider implements DeviceInfoProvider {
    private final Context context;

    public AndroidDeviceInfoProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override public DeviceInfo read() {
        String hardwareId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String model = (android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL).trim();
        return new DeviceInfo(safe(hardwareId), safe(model));
    }

    private static String safe(String text) {
        return text == null || text.isBlank() ? "unknown" : text.trim().replaceAll("\\s+", " ");
    }
}
