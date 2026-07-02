package com.dvid.dcam.camera;

public final class CameraState {
    private final RecordingMode mode;
    private final String currentFileName;
    private final Long startedAtMillis;

    public CameraState() { this(RecordingMode.IDLE, null, null); }
    public CameraState(RecordingMode mode, String currentFileName, Long startedAtMillis) {
        this.mode = mode; this.currentFileName = currentFileName; this.startedAtMillis = startedAtMillis;
    }
    public RecordingMode getMode() { return mode; }
    public String getCurrentFileName() { return currentFileName; }
    public Long getStartedAtMillis() { return startedAtMillis; }
}
