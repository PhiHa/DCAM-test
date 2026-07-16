package com.dvid.dcam.platform.device;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

/** Persistent app rotation preference and Device Owner Wi-Fi control. */
public final class AndroidDeviceSettings {
    private static final String PREFS_NAME = "dcam_device";
    private static final String AUTO_ROTATE = "auto_rotate";
    private final Context context;

    public AndroidDeviceSettings(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean isAutoRotateEnabled() { return prefs().getBoolean(AUTO_ROTATE, true); }

    public void setAutoRotateEnabled(boolean enabled) {
        prefs().edit().putBoolean(AUTO_ROTATE, enabled).apply();
    }

    public boolean isWifiEnabled() {
        WifiManager wifi = context.getSystemService(WifiManager.class);
        return wifi != null && wifi.isWifiEnabled();
    }

    public boolean isDeviceOwner() {
        DevicePolicyManager policy = context.getSystemService(DevicePolicyManager.class);
        return policy != null && policy.isDeviceOwnerApp(context.getPackageName());
    }

    @SuppressWarnings("deprecation")
    public boolean setWifiEnabled(boolean enabled) {
        DevicePolicyManager policy = context.getSystemService(DevicePolicyManager.class);
        WifiManager wifi = context.getSystemService(WifiManager.class);
        return isDeviceOwner() && wifi != null && wifi.setWifiEnabled(enabled);
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
