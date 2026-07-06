package com.dvid.dcam.feature.capture.domain;

/** Immutable capture state safe to expose to presentation code. */
public final class CaptureState {
    private final RecordingMode mode;
    private final String currentFileName;
    private final Long startedAtMillis;

    public CaptureState() {
        this(RecordingMode.IDLE, null, null);
    }

    public CaptureState(RecordingMode mode, String currentFileName, Long startedAtMillis) {
        this.mode = mode;
        this.currentFileName = currentFileName;
        this.startedAtMillis = startedAtMillis;
    }

    public RecordingMode getMode() { return mode; }
    public String getCurrentFileName() { return currentFileName; }
    public Long getStartedAtMillis() { return startedAtMillis; }
}
