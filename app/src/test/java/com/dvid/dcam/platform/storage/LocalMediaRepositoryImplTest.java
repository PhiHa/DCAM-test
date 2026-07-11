package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import com.dvid.dcam.feature.settings.domain.StorageMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

final class LocalMediaRepositoryImplTest {
    @Test void listsOnlyDcamRootsAndMediaInsideThem() throws Exception {
        Path root = Files.createTempDirectory("dcam-media");
        Path video = Files.createDirectories(root.resolve("Media/Video"));
        Files.write(video.resolve("clip.mp4"), new byte[] {1, 2, 3});
        LocalMediaRepositoryImpl browser = new LocalMediaRepositoryImpl(new DcamStorage(root.toFile()));

        List<MediaEntry> roots = browser.list("");
        List<MediaEntry> files = browser.list("Video");

        assertEquals(List.of("Video", "IMP", "Image", "Audio"),
                roots.stream().map(MediaEntry::getName).toList());
        assertEquals("clip.mp4", files.get(0).getName());
        assertEquals("video/mp4", files.get(0).getMimeType());
    }

    @Test void rejectsPathsOutsideMediaRoots() throws Exception {
        Path root = Files.createTempDirectory("dcam-media");
        LocalMediaRepositoryImpl browser = new LocalMediaRepositoryImpl(new DcamStorage(root.toFile()));
        assertThrows(SecurityException.class, () -> browser.list("../private"));
        assertThrows(SecurityException.class, () -> browser.list("Logs"));
    }

    @Test void stagedCaptureIsAbsentFromFinalMediaAndMediaBrowsing() throws Exception {
        Path root = Files.createTempDirectory("dcam-media");
        DcamStorage storage = new DcamStorage(StorageMode.INTERNAL, root.toFile());
        Path staged = storage.outputFile(DcamFileType.VIDEO, "CAM001", "000000",
                LocalDateTime.of(2026, 6, 19, 10, 3, 24), false).toPath();
        Files.write(staged, new byte[] {1, 2, 3});
        LocalMediaRepositoryImpl browser = new LocalMediaRepositoryImpl(storage);

        assertFalse(Files.exists(root.resolve("Media/Video").resolve(staged.getFileName())));
        assertEquals(List.of(), browser.list("Video"));
        assertThrows(SecurityException.class, () -> browser.list("Temp"));
    }
}
