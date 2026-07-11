package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.dvid.dcam.feature.settings.domain.StorageMode;
import java.io.File;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class DcamStorageTest {
    @Test public void stagesVideoImageAndSosOutsideFinalMediaFolders() {
        DcamStorage storage = new DcamStorage(StorageMode.INTERNAL, new File("AppData"));
        LocalDateTime at = LocalDateTime.of(2026, 6, 19, 10, 3, 24);

        assertEquals("AppData/Temp/DCAM_CAM001_000000_20260619_100324.mp4",
                path(storage.outputFile(DcamFileType.VIDEO, "CAM001", "000000", at, false)));
        assertEquals("AppData/Temp/DCAM_CAM001_000000_20260619_100324.jpg",
                path(storage.outputFile(DcamFileType.IMAGE, "CAM001", "000000", at, false)));
        assertEquals("AppData/Temp/DCAM_CAM001_000000_20260619_100324_IMP_enc.mp4",
                path(storage.outputFile(DcamFileType.SOS, "CAM001", "000000", at, true)));
    }

    @Test public void leavesAudioOnItsExistingFinalPath() {
        File file = new DcamStorage(StorageMode.INTERNAL, new File("AppData")).outputFile(
                DcamFileType.AUDIO, "CAM001", "000000",
                LocalDateTime.of(2026, 6, 19, 10, 3, 24), false);

        assertEquals("AppData/Media/Audio/DCAM_CAM001_000000_20260619_100324.aac", path(file));
    }

    @Test public void buildsDeviceOnlyConfigPath() {
        File file = new DcamStorage(StorageMode.INTERNAL, new File("AppData")).configsFile();

        assertEquals("AppData/Config/dcam_config.cson", file.getPath().replace('\\', '/'));
    }

    @Test public void defaultsMissingStorageSettingToAutoAndReadsLegacyInternalAlias() {
        assertEquals(StorageMode.AUTO, StorageMode.from(null));
        assertEquals(StorageMode.AUTO, StorageMode.from(""));
        assertEquals(StorageMode.INTERNAL, StorageMode.from("APP_DATA"));
    }

    @Test public void externalResolutionUsesOneMediaRootAndKeepsConfigInternal() {
        DcamStorage storage = new DcamStorage(
                StorageMode.AUTO,
                StorageMode.EXTERNAL,
                new File("InternalAppData"),
                new File("ExternalAppData"),
                new DcamStorageCapacityPolicy());
        LocalDateTime at = LocalDateTime.of(2026, 6, 19, 10, 3, 24);

        assertEquals("ExternalAppData/Temp/DCAM_CAM001_000000_20260619_100324.mp4",
                path(storage.outputFile(DcamFileType.VIDEO, "CAM001", "000000", at, false)));
        assertEquals("ExternalAppData/Temp/DCAM_CAM001_000000_20260619_100324.jpg",
                path(storage.outputFile(DcamFileType.IMAGE, "CAM001", "000000", at, false)));
        assertEquals("ExternalAppData/Media/Audio/DCAM_CAM001_000000_20260619_100324.aac",
                path(storage.outputFile(DcamFileType.AUDIO, "CAM001", "000000", at, false)));
        assertEquals("InternalAppData/Config/dcam_config.cson", path(storage.configsFile()));
    }

    private static String path(File file) { return file.getPath().replace('\\', '/'); }
}
