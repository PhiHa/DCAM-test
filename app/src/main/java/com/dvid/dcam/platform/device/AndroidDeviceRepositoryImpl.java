package com.dvid.dcam.platform.device;

import android.content.Context;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.StatFs;
import android.provider.Settings;
import com.dvid.dcam.feature.device.application.port.DeviceRepository;
import com.dvid.dcam.feature.device.domain.CapabilityStatus;
import com.dvid.dcam.feature.device.domain.DeviceInfo;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Android implementation of the device repository. */
public final class AndroidDeviceRepositoryImpl implements DeviceRepository {
    private final Context context;

    public AndroidDeviceRepositoryImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override public DeviceInfo readInfo() {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id == null || id.isBlank()) id = "unknown";
        String model = Build.MANUFACTURER + " " + Build.MODEL;
        return new DeviceInfo(sha256(id), model.trim(), serialNumber());
    }

    @Override public DeviceStatus readStatus() {
        int batteryPercent = -1;
        long availableBytes = -1L;
        CapabilityStatus gpsStatus = CapabilityStatus.UNKNOWN;
        try {
            BatteryManager battery = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (battery != null) batteryPercent = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } catch (RuntimeException ignored) {}
        try {
            File storage = context.getExternalFilesDir(null);
            if (storage == null) storage = context.getFilesDir();
            availableBytes = new StatFs(storage.getAbsolutePath()).getAvailableBytes();
        } catch (RuntimeException ignored) {}
        try {
            LocationManager location = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (location == null || !context.getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                gpsStatus = CapabilityStatus.UNAVAILABLE;
            } else {
                gpsStatus = location.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        ? CapabilityStatus.AVAILABLE : CapabilityStatus.DISABLED;
            }
        } catch (RuntimeException ignored) {}
        return new DeviceStatus(batteryPercent, availableBytes, gpsStatus);
    }

    private static String serialNumber() {
        String serial = Build.SERIAL;
        return serial == null || serial.isBlank() || "unknown".equalsIgnoreCase(serial) ? null : serial;
    }

    private static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(bytes.length * 2);
            for (byte value : bytes) out.append(String.format("%02x", value));
            return out.toString();
        } catch (NoSuchAlgorithmException error) {
            return Integer.toHexString(text.hashCode());
        }
    }
}
