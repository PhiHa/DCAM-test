package com.dvid.dcam.app.presentation;

import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.core.feature.domain.FeatureGate;

/** Presentation definition for one app menu tile. */
public final class MainMenuTile {
    private final int viewId;
    private final MainScreen screen;
    private final FeatureGate optionalDeveloperGate;

    MainMenuTile(int viewId, MainScreen screen, FeatureGate optionalDeveloperGate) {
        this.viewId = viewId;
        this.screen = screen;
        this.optionalDeveloperGate = optionalDeveloperGate;
    }

    public int getViewId() { return viewId; }
    public MainScreen getScreen() { return screen; }

    FeatureGate getOptionalDeveloperGate() { return optionalDeveloperGate; }
}
