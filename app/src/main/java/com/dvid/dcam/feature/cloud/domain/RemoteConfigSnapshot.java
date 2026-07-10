package com.dvid.dcam.feature.cloud.domain;

/** Cached remote-config payload plus local apply state. */
public final class RemoteConfigSnapshot {
    public enum Status { NONE, ACCEPTED, DEFERRED, REJECTED }

    private final String revision;
    private final String payloadJson;
    private final Status status;
    private final String lastError;

    public RemoteConfigSnapshot(String revision, String payloadJson, Status status, String lastError) {
        this.revision = emptyToNull(revision);
        this.payloadJson = emptyToNull(payloadJson);
        this.status = status == null ? Status.NONE : status;
        this.lastError = emptyToNull(lastError);
    }

    public static RemoteConfigSnapshot none() {
        return new RemoteConfigSnapshot(null, null, Status.NONE, null);
    }

    public String getRevision() { return revision; }
    public String getPayloadJson() { return payloadJson; }
    public Status getStatus() { return status; }
    public String getLastError() { return lastError; }

    private static String emptyToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
