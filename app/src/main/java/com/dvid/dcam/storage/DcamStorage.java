package com.dvid.dcam.storage;

import android.os.Environment;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DcamStorage {
    private static final DateTimeFormatter FOLDER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final File dcim;

    public DcamStorage() { this(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)); }
    public DcamStorage(File dcim) { this.dcim = dcim; }

    public void ensureFolders(LocalDateTime at) {
        for (DcamFileType type : DcamFileType.values())
            new File(dcim, type.getFolder() + "/" + FOLDER_DATE.format(at)).mkdirs();
    }

    public File outputFile(DcamFileType type, String accountUserId, String policeUserId,
                           LocalDateTime at, boolean encrypted) {
        File dir = new File(dcim, type.getFolder() + "/" + FOLDER_DATE.format(at));
        dir.mkdirs();
        return new File(dir, DcamFileName.build(type, accountUserId, policeUserId, at, encrypted));
    }

    public File configsFile() { return new File(dcim, "configs.cson"); }
}
