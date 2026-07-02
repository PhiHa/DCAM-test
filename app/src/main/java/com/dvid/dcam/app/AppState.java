package com.dvid.dcam.app;

import com.dvid.dcam.camera.CameraState;
import com.dvid.dcam.config.DcamConfig;

public final class AppState {
    private Screen screen;
    private final DcamConfig config;
    private CameraState camera;

    public AppState(DcamConfig config, CameraState camera) {
        this.screen = Screen.CAMERA; this.config = config; this.camera = camera;
    }
    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
    public DcamConfig getConfig() { return config; }
    public CameraState getCamera() { return camera; }
    public void setCamera(CameraState camera) { this.camera = camera; }
}
