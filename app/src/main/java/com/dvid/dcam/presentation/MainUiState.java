package com.dvid.dcam.presentation;

import com.dvid.dcam.domain.model.CaptureState;
import com.dvid.dcam.domain.model.DeviceStatus;

/** Immutable state rendered by MainActivity. */
public final class MainUiState {
    private final MainScreen screen;
    private final CaptureState capture;
    private final DeviceStatus deviceStatus;
    private final MediaBrowserState mediaBrowser;
    private final String message;

    public MainUiState(MainScreen screen, CaptureState capture, DeviceStatus deviceStatus,
                       MediaBrowserState mediaBrowser, String message) {
        this.screen = screen;
        this.capture = capture;
        this.deviceStatus = deviceStatus;
        this.mediaBrowser = mediaBrowser;
        this.message = message;
    }

    public MainScreen getScreen() { return screen; }
    public CaptureState getCapture() { return capture; }
    public DeviceStatus getDeviceStatus() { return deviceStatus; }
    public MediaBrowserState getMediaBrowser() { return mediaBrowser; }
    public String getMessage() { return message; }

    public MainUiState withScreen(MainScreen next) {
        return new MainUiState(next, capture, deviceStatus, mediaBrowser, message);
    }

    public MainUiState withCapture(CaptureState next, String nextMessage) {
        return new MainUiState(screen, next, deviceStatus, mediaBrowser, nextMessage);
    }

    public MainUiState withMessage(String nextMessage) {
        return new MainUiState(screen, capture, deviceStatus, mediaBrowser, nextMessage);
    }

    public MainUiState withDeviceStatus(DeviceStatus nextStatus) {
        return new MainUiState(screen, capture, nextStatus, mediaBrowser, message);
    }

    public MainUiState withMediaBrowser(MediaBrowserState nextBrowser) {
        return new MainUiState(screen, capture, deviceStatus, nextBrowser, message);
    }
}
