package com.dvid.dcam.storage;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DcamMediaStore {
    private static final DateTimeFormatter FOLDER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DcamMediaStore() {}

    public static String relativePath(DcamFileType type, LocalDateTime at) {
        return "DCIM/" + type.getFolder() + "/" + FOLDER_DATE.format(at);
    }
    public static ContentValues values(DcamMediaFile mediaFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, mediaFile.getFileName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, mediaFile.getType().getMimeType());
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath(mediaFile.getType(), mediaFile.getCreatedAt()));
        return values;
    }
    public static Uri imageCollection() { return MediaStore.Images.Media.EXTERNAL_CONTENT_URI; }
    public static Uri videoCollection() { return MediaStore.Video.Media.EXTERNAL_CONTENT_URI; }
}
