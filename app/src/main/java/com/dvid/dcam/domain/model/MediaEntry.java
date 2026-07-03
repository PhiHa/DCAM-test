package com.dvid.dcam.domain.model;

/** Read-only media-browser entry rooted inside DCAM-managed media storage. */
public final class MediaEntry {
    private final String name;
    private final String relativePath;
    private final String absolutePath;
    private final boolean directory;
    private final long sizeBytes;
    private final long modifiedAtMillis;
    private final String mimeType;

    public MediaEntry(String name, String relativePath, String absolutePath, boolean directory,
                      long sizeBytes, long modifiedAtMillis, String mimeType) {
        this.name = name;
        this.relativePath = relativePath;
        this.absolutePath = absolutePath;
        this.directory = directory;
        this.sizeBytes = sizeBytes;
        this.modifiedAtMillis = modifiedAtMillis;
        this.mimeType = mimeType;
    }

    public String getName() { return name; }
    public String getRelativePath() { return relativePath; }
    public String getAbsolutePath() { return absolutePath; }
    public boolean isDirectory() { return directory; }
    public long getSizeBytes() { return sizeBytes; }
    public long getModifiedAtMillis() { return modifiedAtMillis; }
    public String getMimeType() { return mimeType; }
}
