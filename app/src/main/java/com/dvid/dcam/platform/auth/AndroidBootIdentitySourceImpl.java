package com.dvid.dcam.platform.auth;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import com.dvid.dcam.feature.auth.application.port.BootIdentitySource;

/** Uses Android's boot counter, with a stable boot-epoch fallback for restricted devices. */
public final class AndroidBootIdentitySourceImpl implements BootIdentitySource {
    private final Context context;

    public AndroidBootIdentitySourceImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public String currentBootId() {
        int bootCount = Settings.Global.getInt(
                context.getContentResolver(), Settings.Global.BOOT_COUNT, -1);
        if (bootCount >= 0) return "boot-count:" + bootCount;
        long bootEpochMinutes =
                (System.currentTimeMillis() - SystemClock.elapsedRealtime()) / 60_000L;
        return "boot-epoch-minute:" + bootEpochMinutes;
    }
}
