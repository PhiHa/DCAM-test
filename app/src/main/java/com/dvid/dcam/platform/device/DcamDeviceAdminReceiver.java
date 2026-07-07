package com.dvid.dcam.platform.device;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import com.dvid.dcam.platform.logging.DcamLogger;

/** Device owner/admin entry point used for managed kiosk provisioning. */
public final class DcamDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override public void onEnabled(Context context, Intent intent) {
        DcamLogger.i("DCAM device admin enabled");
        new DcamKioskController(context).applyActiveKioskPolicy();
    }

    @Override public void onDisabled(Context context, Intent intent) {
        DcamLogger.i("DCAM device admin disabled");
    }
}
