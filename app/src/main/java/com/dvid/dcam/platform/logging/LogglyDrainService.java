package com.dvid.dcam.platform.logging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.dvid.dcam.R;
import androidx.room.InvalidationTracker;
import com.dvid.dcam.platform.database.AppDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LogglyDrainService extends Service {
    private static final String CHANNEL_ID = "dcam_loggly";
    private static final int NOTIFICATION_ID = 1002;
    private static final long MIN_RETRY_WAIT_MS = 2_000L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Object wakeLock = new Object();
    private final IBinder binder = new Binder();
    private InvalidationTracker.Observer observer;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private volatile boolean networkAvailable;
    private volatile boolean clientBound;
    private volatile boolean clientEverBound;
    private volatile boolean stopped;

    public static boolean start(Context context) {
        try {
            ContextCompat.startForegroundService(context,
                    new Intent(context, LogglyDrainService.class));
            return true;
        } catch (RuntimeException error) {
            LogglyDiagnostics.write(context, "ERROR", "Could not start Loggly drain service", error);
            LogUploadScheduler.scheduleNow(context);
            return false;
        }
    }

    @Override public void onCreate() {
        super.onCreate();
        LogUploadScheduler.setContinuousDrainEnabled(true);
        LogUploadScheduler.cancel(this);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                getString(R.string.loggly_notification_channel), NotificationManager.IMPORTANCE_MIN);
        channel.setDescription(getString(R.string.loggly_notification_channel_description));
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_recording_notification)
                .setContentTitle(getString(R.string.loggly_notification_title))
                .setContentText(getString(R.string.loggly_notification_text))
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        ensureObserver();
        registerNetworkCallback();
        executor.execute(this::drainLoop);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void drainLoop() {
        while (!stopped) {
            ensureObserver();
            if (!clientBound && clientEverBound && !hasDeliverableEvents()) {
                stopForeground(STOP_FOREGROUND_REMOVE);
                stopSelf();
                return;
            }
            if (!networkAvailable) {
                waitForWake(0L);
                continue;
            }
            Long retryAt;
            try {
                retryAt = LogglyOutboxSender.sendPending(getApplicationContext(), () -> stopped);
            } catch (RuntimeException error) {
                LogglyDiagnostics.write(getApplicationContext(), "ERROR",
                        "Loggly drain loop failed", error);
                retryAt = System.currentTimeMillis() + 10_000L;
            }
            long waitMs = retryAt == null
                    ? (observer == null ? 10_000L : 0L)
                    : Math.max(MIN_RETRY_WAIT_MS, retryAt - System.currentTimeMillis());
            waitForWake(waitMs);
        }
    }

    private boolean hasDeliverableEvents() {
        try {
            return LogglyOutboxSender.hasDeliverableEvents(getApplicationContext());
        } catch (RuntimeException error) {
            LogglyDiagnostics.write(getApplicationContext(), "ERROR",
                    "Could not inspect Loggly outbox", error);
            return true;
        }
    }

    private void waitForWake(long waitMs) {
        synchronized (wakeLock) {
            try {
                wakeLock.wait(waitMs);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                stopped = true;
            }
        }
    }

    private void registerNetworkCallback() {
        connectivityManager = getSystemService(ConnectivityManager.class);
        if (connectivityManager == null) return;
        networkAvailable = hasValidatedNetwork(connectivityManager.getActiveNetwork());
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override public void onAvailable(Network network) { updateNetworkState(); }
            @Override public void onLost(Network network) { updateNetworkState(); }
            @Override public void onCapabilitiesChanged(Network network, NetworkCapabilities capabilities) {
                updateNetworkState();
            }
        };
        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } catch (RuntimeException error) {
            LogglyDiagnostics.write(getApplicationContext(), "ERROR",
                    "Could not observe network state", error);
            networkCallback = null;
        }
    }

    private void updateNetworkState() {
        networkAvailable = connectivityManager != null
                && hasValidatedNetwork(connectivityManager.getActiveNetwork());
        synchronized (wakeLock) { wakeLock.notifyAll(); }
    }

    private boolean hasValidatedNetwork(Network network) {
        if (connectivityManager == null || network == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private void ensureObserver() {
        if (observer != null) return;
        try {
            InvalidationTracker.Observer next = new InvalidationTracker.Observer("pending_logs") {
                @Override public void onInvalidated(java.util.Set<String> tables) {
                    synchronized (wakeLock) { wakeLock.notifyAll(); }
                }
            };
            AppDatabase.get(getApplicationContext()).getInvalidationTracker().addObserver(next);
            observer = next;
        } catch (RuntimeException error) {
            LogglyDiagnostics.write(getApplicationContext(), "ERROR",
                    "Loggly observer could not open outbox database", error);
            DcamLogger.sendDirect("Loggly observer could not open outbox database", error);
        }
    }

    @Override public void onDestroy() {
        stopped = true;
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (RuntimeException error) {
                LogglyDiagnostics.write(getApplicationContext(), "WARN",
                        "Could not remove network observer", error);
            }
        }
        if (observer != null) {
            try {
                AppDatabase.get(getApplicationContext()).getInvalidationTracker().removeObserver(observer);
            } catch (RuntimeException error) {
                LogglyDiagnostics.write(getApplicationContext(), "WARN",
                        "Could not remove Loggly database observer", error);
            }
        }
        synchronized (wakeLock) { wakeLock.notifyAll(); }
        executor.shutdownNow();
        super.onDestroy();
    }

    @Nullable @Override public IBinder onBind(Intent intent) {
        clientBound = true;
        clientEverBound = true;
        synchronized (wakeLock) { wakeLock.notifyAll(); }
        return binder;
    }

    @Override public boolean onUnbind(Intent intent) {
        clientBound = false;
        synchronized (wakeLock) { wakeLock.notifyAll(); }
        return false;
    }
}
