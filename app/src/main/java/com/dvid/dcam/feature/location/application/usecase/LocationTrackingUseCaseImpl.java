package com.dvid.dcam.feature.location.application.usecase;

import com.dvid.dcam.feature.location.application.port.LocationSource;
import com.dvid.dcam.feature.location.domain.GpsCoordinate;
import java.util.function.Consumer;

public final class LocationTrackingUseCaseImpl implements LocationTrackingUseCase {
    private final LocationSettingsUseCase settings;
    private final LocationSource source;
    private Consumer<GpsCoordinate> consumer;

    public LocationTrackingUseCaseImpl(LocationSettingsUseCase settings, LocationSource source) {
        if (settings == null || source == null) throw new IllegalArgumentException("GPS dependencies are required");
        this.settings = settings;
        this.source = source;
    }
    @Override public synchronized void start(Consumer<GpsCoordinate> callback) {
        if (callback == null) throw new IllegalArgumentException("callback is required");
        consumer = callback;
        source.start(settings.currentSettings(), callback);
    }
    @Override public synchronized void restart() {
        if (consumer != null) source.start(settings.currentSettings(), consumer);
    }
    @Override public synchronized void stop() {
        source.stop();
        consumer = null;
    }
    @Override public GpsCoordinate latestCoordinate() { return source.latestCoordinate(); }
}
