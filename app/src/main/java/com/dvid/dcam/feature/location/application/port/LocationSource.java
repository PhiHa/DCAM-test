package com.dvid.dcam.feature.location.application.port;

import com.dvid.dcam.feature.location.domain.GpsCoordinate;
import com.dvid.dcam.feature.location.domain.GpsSettings;
import java.util.function.Consumer;

public interface LocationSource {
    void start(GpsSettings settings, Consumer<GpsCoordinate> onCoordinate);
    void stop();
    GpsCoordinate latestCoordinate();
}
