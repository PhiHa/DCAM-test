package com.dvid.dcam.platform.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import com.dvid.dcam.feature.location.application.port.LocationSource;
import com.dvid.dcam.feature.location.domain.GpsCoordinate;
import com.dvid.dcam.feature.location.domain.GpsMode;
import com.dvid.dcam.feature.location.domain.GpsSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Foreground Activity-bound LocationManager source. */
public final class AndroidLocationSourceImpl implements LocationSource {
    private final Context context;
    private final LocationManager locationManager;
    private final AndroidGmapLocationSourceImpl gmapSource;
    private final LocationListener listener = this::accept;
    private volatile GpsCoordinate latest;
    private Consumer<GpsCoordinate> consumer;

    public AndroidLocationSourceImpl(Context context) {
        this.context = context.getApplicationContext();
        locationManager = this.context.getSystemService(LocationManager.class);
        gmapSource = new AndroidGmapLocationSourceImpl(this.context);
    }

    @Override public synchronized void start(GpsSettings settings, Consumer<GpsCoordinate> onCoordinate) {
        if (settings == null || onCoordinate == null) throw new IllegalArgumentException("GPS arguments are required");
        stopUpdates();
        gmapSource.stop();
        consumer = onCoordinate;
        if (settings.getMode() == GpsMode.GMAP) {
            gmapSource.start(settings, this::acceptCoordinate);
            return;
        }
        if (locationManager == null) return;
        Location newest = null;
        for (String provider : providers(settings.getMode())) {
            try {
                Location cached = locationManager.getLastKnownLocation(provider);
                if (cached != null && (newest == null || cached.getTime() > newest.getTime())) newest = cached;
                locationManager.requestLocationUpdates(provider,
                        settings.getReportIntervalSeconds() * 1000L,
                        settings.getUpdateDistanceMeters(), listener, Looper.getMainLooper());
            } catch (SecurityException | IllegalArgumentException ignored) { }
        }
        if (newest != null) accept(newest);
    }

    @Override public synchronized void stop() {
        stopUpdates();
        gmapSource.stop();
        consumer = null;
        latest = null;
    }

    @Override public GpsCoordinate latestCoordinate() { return latest; }

    private List<String> providers(GpsMode mode) {
        List<String> result = new ArrayList<>();
        if (hasFinePermission() && enabled(LocationManager.GPS_PROVIDER)) result.add(LocationManager.GPS_PROVIDER);
        if (mode == GpsMode.GPS_AGPS && hasCoarsePermission()
                && enabled(LocationManager.NETWORK_PROVIDER)) result.add(LocationManager.NETWORK_PROVIDER);
        return result;
    }

    private boolean enabled(String provider) {
        try { return locationManager.isProviderEnabled(provider); }
        catch (RuntimeException ignored) { return false; }
    }
    private boolean hasFinePermission() {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    private boolean hasCoarsePermission() {
        return hasFinePermission() || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void accept(Location location) {
        if (location == null) return;
        try {
            GpsCoordinate coordinate = new GpsCoordinate(location.getLatitude(), location.getLongitude());
            latest = coordinate;
            Consumer<GpsCoordinate> callback = consumer;
            if (callback != null) callback.accept(coordinate);
        } catch (IllegalArgumentException ignored) { }
    }

    private void acceptCoordinate(GpsCoordinate coordinate) {
        if (coordinate == null) return;
        latest = coordinate;
        Consumer<GpsCoordinate> callback = consumer;
        if (callback != null) callback.accept(coordinate);
    }
    private void stopUpdates() {
        if (locationManager == null) return;
        try { locationManager.removeUpdates(listener); }
        catch (RuntimeException ignored) { }
    }
}
