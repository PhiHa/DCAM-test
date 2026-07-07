package com.dvid.dcam.platform.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dvid.dcam.platform.logging.DcamLogger;

/** Re-applies kiosk policy after boot or app update and opens DCAM when policy permits it. */
public final class DcamBootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            return;
        }

        DcamKioskController kiosk = new DcamKioskController(context);
        kiosk.applyActiveKioskPolicy();
        if (kiosk.isDeviceOwner() || kiosk.isDefaultHome()) startDcam(context, action);
    }

    private static void startDcam(Context context, String action) {
        try {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (launch == null) {
                DcamLogger.i("No launch intent available after " + action);
                return;
            }
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(launch);
        } catch (RuntimeException error) {
            DcamLogger.w("Could not open DCAM after " + action, error);
        }
    }
}
