package com.dvid.dcam.app.presentation;

import com.dvid.dcam.R;
import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.feature.settings.domain.FeatureGate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Ordered menu and feature-gate routing model for the app shell. */
public final class MainMenuModel {
    private static final List<MainMenuTile> TILES = List.of(
            tile(R.id.files, MainScreen.FILES, FeatureGate.MEDIA_BROWSER),
            tile(R.id.record_settings, MainScreen.RECORD_SETTINGS, FeatureGate.RECORDING_SETTINGS),
            tile(R.id.camera_settings, MainScreen.CAMERA_SETTINGS, FeatureGate.CAMERA_SETTINGS),
            tile(R.id.video_stream_settings, MainScreen.VIDEO_STREAM_SETTINGS, FeatureGate.VIDEO_STREAMING),
            tile(R.id.audio_settings, MainScreen.AUDIO_SETTINGS, FeatureGate.AUDIO_SETTINGS),
            tile(R.id.storage_settings, MainScreen.STORAGE_SETTINGS, FeatureGate.STORAGE_SETTINGS),
            tile(R.id.gps_settings, MainScreen.GPS_SETTINGS, FeatureGate.GPS),
            tile(R.id.device_settings, MainScreen.DEVICE_SETTINGS, FeatureGate.DEVICE_SETTINGS),
            tile(R.id.user_settings, MainScreen.USER_SETTINGS, FeatureGate.SECURITY_ENCRYPTION),
            tile(R.id.server_settings, MainScreen.SERVER_SETTINGS, FeatureGate.CLOUD_NETWORK),
            tile(R.id.transfer_settings, MainScreen.TRANSFER_SETTINGS, FeatureGate.TRANSFER),
            tile(R.id.about, MainScreen.ABOUT, null));

    public List<MainMenuTile> visibleTiles(Predicate<FeatureGate> isEnabled) {
        List<MainMenuTile> visible = new ArrayList<>();
        for (MainMenuTile tile : TILES) {
            if (tile.isVisible(isEnabled)) visible.add(tile);
        }
        return visible;
    }

    public MainScreen safeScreen(MainScreen screen, Predicate<FeatureGate> isEnabled) {
        FeatureGate feature = featureFor(screen);
        return feature == null || isEnabled.test(feature) ? screen : MainScreen.MENU;
    }

    private FeatureGate featureFor(MainScreen screen) {
        for (MainMenuTile tile : TILES) {
            if (tile.getScreen() == screen) return tile.getFeature();
        }
        return null;
    }

    private static MainMenuTile tile(int viewId, MainScreen screen, FeatureGate feature) {
        return new MainMenuTile(viewId, screen, feature);
    }
}
