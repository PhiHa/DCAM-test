package com.dvid.dcam.platform.recording;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.dvid.dcam.R;

/**
 * Keeps an active recording visible to Android and reduces background process risk.
 * Camera ownership remains in the CameraX adapter until recovery design is approved.
 */
public final class RecordingForegroundService extends Service {
    private static final String CHANNEL_ID = "dcam_recording";
    private static final int NOTIFICATION_ID = 1001;
    private static final String EXTRA_FILE_NAME = "file_name";

    public static boolean start(Context context, String fileName) {
        Intent intent = new Intent(context, RecordingForegroundService.class)
                .putExtra(EXTRA_FILE_NAME, fileName);
        try {
            ContextCompat.startForegroundService(context, intent);
            return true;
        } catch (RuntimeException error) {
            return false;
        }
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, RecordingForegroundService.class));
    }

    @Override public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "DCAM recording", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Shows when BodyCamera recording is active");
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        String fileName = intent == null ? null : intent.getStringExtra(EXTRA_FILE_NAME);
        String text = fileName == null || fileName.isBlank() ? "Recording in progress" : fileName;
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("DCAM recording")
                .setContentText(text)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                serviceType |= ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
            }
            startForeground(NOTIFICATION_ID, notification, serviceType);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        return START_NOT_STICKY;
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
