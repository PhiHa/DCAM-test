package com.dvid.dcam.app.presentation;

import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.feature.settings.domain.FeatureGate;
import java.util.function.Predicate;

/** Presentation definition for one app menu tile. */
public final class MainMenuTile {
    private final int viewId;
    private final MainScreen screen;
    private final FeatureGate feature;

    MainMenuTile(int viewId, MainScreen screen, FeatureGate feature) {
        this.viewId = viewId;
        this.screen = screen;
        this.feature = feature;
    }

    public int getViewId() { return viewId; }
    public MainScreen getScreen() { return screen; }
    public FeatureGate getFeature() { return feature; }

    public boolean isVisible(Predicate<FeatureGate> isEnabled) {
        return feature == null || isEnabled.test(feature);
    }
}
