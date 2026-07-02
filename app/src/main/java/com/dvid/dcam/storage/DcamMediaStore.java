package com.dvid.dcam.storage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DcamMediaStore {
    private static final DateTimeFormatter FOLDER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DcamMediaStore() {}

    public static String relativePath(DcamFileType type, LocalDateTime at) {
        return "DCIM/" + type.getFolder() + "/" + FOLDER_DATE.format(at);
    }
    public static ContentValues values(DcamFileType type, String fileName, LocalDateTime at) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, type.getMimeType());
        if (Build.VERSION.SDK_INT >= 29) values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath(type, at));
        return values;
    }
    public static Uri imageCollection() { return MediaStore.Images.Media.EXTERNAL_CONTENT_URI; }
    public static Uri videoCollection() { return MediaStore.Video.Media.EXTERNAL_CONTENT_URI; }
    public static Uri audioCollection() { return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; }
    public static Uri insertAudio(ContentResolver resolver, String fileName, LocalDateTime at) {
        return resolver.insert(audioCollection(), values(DcamFileType.AUDIO, fileName, at));
    }
}
