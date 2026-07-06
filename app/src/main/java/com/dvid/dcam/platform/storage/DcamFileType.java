package com.dvid.dcam.platform.storage;

public enum DcamFileType {
    VIDEO("video", "mp4", "", "video/mp4"),
    SOS("SOS", "mp4", "SOS", "video/mp4"),
    IMAGE("image", "jpg", "", "image/jpeg"),
    AUDIO("audio", "m4a", "", "audio/mp4");

    private final String folder, extension, marker, mimeType;

    DcamFileType(String folder, String extension, String marker, String mimeType) {
        this.folder = folder; this.extension = extension; this.marker = marker; this.mimeType = mimeType;
    }
    public String getFolder() { return folder; }
    public String getExtension() { return extension; }
    public String getMarker() { return marker; }
    public String getMimeType() { return mimeType; }
}
