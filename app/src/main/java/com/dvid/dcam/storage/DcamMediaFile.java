package com.dvid.dcam.storage;

import java.io.File;
import java.time.LocalDateTime;

public final class DcamMediaFile {
    private final DcamFileType type;
    private final String fileName;
    private final File file;
    private final LocalDateTime createdAt;

    public DcamMediaFile(DcamFileType type, String fileName, File file, LocalDateTime createdAt) {
        this.type = type;
        this.fileName = fileName;
        this.file = file;
        this.createdAt = createdAt;
    }

    public DcamFileType getType() { return type; }
    public String getFileName() { return fileName; }
    public File getFile() { return file; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
