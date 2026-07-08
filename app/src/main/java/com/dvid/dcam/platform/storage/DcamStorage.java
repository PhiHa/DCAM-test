package com.dvid.dcam.platform.storage;

import android.content.Context;
import android.os.Environment;
import com.dvid.dcam.BuildConfig;
import java.io.File;
import java.time.LocalDateTime;

public final class DcamStorage {
    private final DcamStorageMode mode;
    private final File root;

    public DcamStorage() { this(DcamStorageMode.PUBLIC_DCIM, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)); }
    public DcamStorage(File root) { this(DcamStorageMode.PUBLIC_DCIM, root); }
    public DcamStorage(DcamStorageMode mode, File root) { this.mode = mode; this.root = root; }

    public static DcamStorage from(Context context) {
        DcamStorageMode mode = DcamStorageMode.from(BuildConfig.STORAGE_MODE);
        File root;
        if (mode == DcamStorageMode.PUBLIC_DCIM) root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        else {
            root = context.getExternalFilesDir(null);
            if (root == null) root = context.getFilesDir();
        }
        return new DcamStorage(mode, root);
    }

    public DcamStorageMode getMode() { return mode; }
    public boolean isPublicDcim() { return mode == DcamStorageMode.PUBLIC_DCIM; }
    public File rootDirectory() { return new File(root, "Media"); }

    public void ensureFolders() {
        for (DcamFileType type : DcamFileType.values())
            new File(rootDirectory(), type.getFolder()).mkdirs();
        new File(root, "Temp").mkdirs();
    }

    public File outputFile(DcamFileType type, String accountUserId, String policeUserId,
                           LocalDateTime at, boolean encrypted) {
        return prepareFile(mediaFile(type, accountUserId, policeUserId, at, encrypted));
    }

    public DcamMediaFile mediaFile(DcamFileType type, String accountUserId, String policeUserId,
                                   LocalDateTime at, boolean encrypted) {
        File dir = new File(rootDirectory(), type.getFolder());
        String fileName = DcamFileName.build(type, accountUserId, policeUserId, at, encrypted);
        return new DcamMediaFile(type, fileName, new File(dir, fileName), at);
    }

    public File prepareFile(DcamMediaFile mediaFile) {
        File parent = mediaFile.getFile().getParentFile();
        if (parent != null) parent.mkdirs();
        return mediaFile.getFile();
    }

    public File configsFile() { return new File(new File(root, "Config"), "dcam_config.cson"); }

    public File legacyConfigsFile() { return new File(root, "configs.cson"); }
}
