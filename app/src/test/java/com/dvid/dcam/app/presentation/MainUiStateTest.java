package com.dvid.dcam.app.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.feature.capture.domain.CaptureState;
import com.dvid.dcam.feature.device.domain.CapabilityStatus;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.presentation.MediaBrowserState;
import org.junit.jupiter.api.Test;

final class MainUiStateTest {
    @Test void screenAndCaptureUpdatesPreserveDeviceStatus() {
        DeviceStatus device = new DeviceStatus(75, 1024L, CapabilityStatus.AVAILABLE);
        MainUiState initial = new MainUiState(
                MainScreen.CAMERA, new CaptureState(), device, MediaBrowserState.root(), null);

        MainUiState menu = initial.withScreen(MainScreen.MENU);
        MainUiState message = menu.withMessage("ready");

        assertSame(device, message.getDeviceStatus());
        assertEquals(MainScreen.MENU, message.getScreen());
        assertEquals("ready", message.getMessage());
    }
}
