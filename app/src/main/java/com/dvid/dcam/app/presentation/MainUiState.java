package com.dvid.dcam.app.presentation;

import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.feature.capture.domain.CaptureState;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.auth.domain.OperatorSession;
import com.dvid.dcam.feature.media.presentation.MediaBrowserState;

/** Immutable state rendered by MainActivity. */
public final class MainUiState {
    private final MainScreen screen;
    private final CaptureState capture;
    private final DeviceStatus deviceStatus;
    private final MediaBrowserState mediaBrowser;
    private final String message;
    private final OperatorSession operatorSession;
    private final boolean authenticationBusy;

    public MainUiState(MainScreen screen, CaptureState capture, DeviceStatus deviceStatus,
                       MediaBrowserState mediaBrowser, String message) {
        this(screen, capture, deviceStatus, mediaBrowser, message, null, false);
    }

    public MainUiState(
            MainScreen screen,
            CaptureState capture,
            DeviceStatus deviceStatus,
            MediaBrowserState mediaBrowser,
            String message,
            OperatorSession operatorSession,
            boolean authenticationBusy) {
        this.screen = screen;
        this.capture = capture;
        this.deviceStatus = deviceStatus;
        this.mediaBrowser = mediaBrowser;
        this.message = message;
        this.operatorSession = operatorSession;
        this.authenticationBusy = authenticationBusy;
    }

    public MainScreen getScreen() { return screen; }
    public CaptureState getCapture() { return capture; }
    public DeviceStatus getDeviceStatus() { return deviceStatus; }
    public MediaBrowserState getMediaBrowser() { return mediaBrowser; }
    public String getMessage() { return message; }
    public OperatorSession getOperatorSession() { return operatorSession; }
    public boolean isAuthenticationBusy() { return authenticationBusy; }

    public MainUiState withScreen(MainScreen next) {
        return copy(next, capture, deviceStatus, mediaBrowser, message, operatorSession,
                authenticationBusy);
    }

    public MainUiState withCapture(CaptureState next, String nextMessage) {
        return copy(screen, next, deviceStatus, mediaBrowser, nextMessage, operatorSession,
                authenticationBusy);
    }

    public MainUiState withMessage(String nextMessage) {
        return copy(screen, capture, deviceStatus, mediaBrowser, nextMessage, operatorSession,
                authenticationBusy);
    }

    public MainUiState withDeviceStatus(DeviceStatus nextStatus) {
        return copy(screen, capture, nextStatus, mediaBrowser, message, operatorSession,
                authenticationBusy);
    }

    public MainUiState withMediaBrowser(MediaBrowserState nextBrowser) {
        return copy(screen, capture, deviceStatus, nextBrowser, message, operatorSession,
                authenticationBusy);
    }

    public MainUiState withAuthentication(
            OperatorSession nextSession,
            boolean busy,
            MainScreen nextScreen,
            String nextMessage) {
        return copy(nextScreen, capture, deviceStatus, mediaBrowser, nextMessage, nextSession, busy);
    }

    public MainUiState withAuthenticationBusy(boolean busy, String nextMessage) {
        return copy(screen, capture, deviceStatus, mediaBrowser, nextMessage, operatorSession, busy);
    }

    private static MainUiState copy(
            MainScreen screen,
            CaptureState capture,
            DeviceStatus deviceStatus,
            MediaBrowserState mediaBrowser,
            String message,
            OperatorSession operatorSession,
            boolean authenticationBusy) {
        return new MainUiState(screen, capture, deviceStatus, mediaBrowser, message,
                operatorSession, authenticationBusy);
    }
}
