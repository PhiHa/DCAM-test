package com.dvid.dcam.permissions;

import android.Manifest;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;

public final class DcamPermissions {
    private DcamPermissions() {}
    public static String[] runtime() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        if (Build.VERSION.SDK_INT >= 33) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
        } else permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissions.toArray(new String[0]);
    }
}
