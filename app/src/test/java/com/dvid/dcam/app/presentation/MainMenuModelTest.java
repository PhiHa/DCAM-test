package com.dvid.dcam.app.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.feature.settings.domain.FeatureGate;
import java.util.List;
import org.junit.jupiter.api.Test;

final class MainMenuModelTest {
    @Test void visibleTilesKeepDeclaredOrderAndAlwaysIncludeAbout() {
        MainMenuModel model = new MainMenuModel();

        List<MainScreen> screens = model.visibleTiles(feature ->
                        feature == FeatureGate.MEDIA_BROWSER || feature == FeatureGate.GPS)
                .stream()
                .map(MainMenuTile::getScreen)
                .toList();

        assertEquals(List.of(MainScreen.FILES, MainScreen.GPS_SETTINGS, MainScreen.ABOUT), screens);
    }

    @Test void disabledFeatureScreenFallsBackToMenu() {
        MainMenuModel model = new MainMenuModel();

        MainScreen screen = model.safeScreen(MainScreen.GPS_SETTINGS, feature -> false);

        assertEquals(MainScreen.MENU, screen);
    }
}
