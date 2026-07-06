package com.dvid.dcam.feature.capture.domain;

/** Immutable application event emitted after platform capture callbacks are translated. */
public final class CaptureEvent {
    public enum Type {
        RECORDING_STARTED,
        RECORDING_COMPLETED,
        PHOTO_SAVED,
        ERROR
    }

    private final Type type;
    private final RecordingMode mode;
    private final String fileName;
    private final String operation;
    private final String message;

    private CaptureEvent(
            Type type, RecordingMode mode, String fileName, String operation, String message) {
        this.type = type;
        this.mode = mode;
        this.fileName = fileName;
        this.operation = operation;
        this.message = message;
    }

    public static CaptureEvent recordingStarted(RecordingMode mode, String fileName) {
        return new CaptureEvent(Type.RECORDING_STARTED, mode, fileName, null, null);
    }

    public static CaptureEvent recordingCompleted(String fileName) {
        return new CaptureEvent(Type.RECORDING_COMPLETED, RecordingMode.IDLE, fileName, null, null);
    }

    public static CaptureEvent photoSaved(String fileName) {
        return new CaptureEvent(Type.PHOTO_SAVED, null, fileName, null, null);
    }

    public static CaptureEvent error(String operation, String message) {
        return new CaptureEvent(Type.ERROR, RecordingMode.IDLE, null, operation, message);
    }

    public Type getType() { return type; }
    public RecordingMode getMode() { return mode; }
    public String getFileName() { return fileName; }
    public String getOperation() { return operation; }
    public String getMessage() { return message; }
}
