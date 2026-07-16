package com.dvid.dcam.feature.location.application.usecase;

import com.dvid.dcam.feature.location.domain.GpsCoordinate;
import java.util.function.Consumer;

public interface LocationTrackingUseCase {
    void start(Consumer<GpsCoordinate> onCoordinate);
    void restart();
    void stop();
    GpsCoordinate latestCoordinate();
}
