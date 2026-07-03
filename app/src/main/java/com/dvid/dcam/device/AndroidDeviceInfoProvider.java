package com.dvid.dcam.device;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.StatFs;
import android.provider.Settings;
import com.dvid.dcam.domain.model.CapabilityStatus;
import com.dvid.dcam.domain.model.DeviceInfo;
import com.dvid.dcam.domain.model.DeviceStatus;
import com.dvid.dcam.domain.service.DeviceService;

public final class AndroidDeviceInfoProvider implements DeviceService {
    private final Context context;

    public AndroidDeviceInfoProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override public DeviceInfo read() {
        String hardwareId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String model = (android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL).trim();
        return new DeviceInfo(safe(hardwareId), safe(model));
    }

    @Override public DeviceStatus readStatus() {
        BatteryManager batteryManager = context.getSystemService(BatteryManager.class);
        int battery = batteryManager == null ? DeviceStatus.UNKNOWN_BATTERY_PERCENT
                : batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if (battery < 0 || battery > 100) battery = DeviceStatus.UNKNOWN_BATTERY_PERCENT;

        long availableBytes = DeviceStatus.UNKNOWN_STORAGE_BYTES;
        try {
            java.io.File storage = context.getExternalFilesDir(null);
            if (storage == null) storage = context.getFilesDir();
            availableBytes = new StatFs(storage.getAbsolutePath()).getAvailableBytes();
        } catch (RuntimeException ignored) { }

        return new DeviceStatus(battery, availableBytes, gpsStatus());
    }

    private CapabilityStatus gpsStatus() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            return CapabilityStatus.UNAVAILABLE;
        }
        try {
            LocationManager manager = context.getSystemService(LocationManager.class);
            return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    ? CapabilityStatus.AVAILABLE : CapabilityStatus.DISABLED;
        } catch (RuntimeException error) {
            return CapabilityStatus.UNKNOWN;
        }
    }

    private static String safe(String text) {
        return text == null || text.isBlank() ? "unknown" : text.trim().replaceAll("\\s+", " ");
    }
}
