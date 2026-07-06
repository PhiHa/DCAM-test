package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

final class LocalMediaRepositoryImplTest {
    @Test void listsOnlyDcamRootsAndMediaInsideThem() throws Exception {
        Path root = Files.createTempDirectory("dcam-media");
        Path day = Files.createDirectories(root.resolve("video/2026-07-03"));
        Files.write(day.resolve("clip.mp4"), new byte[] {1, 2, 3});
        LocalMediaRepositoryImpl browser = new LocalMediaRepositoryImpl(new DcamStorage(root.toFile()));

        List<MediaEntry> roots = browser.list("");
        List<MediaEntry> files = browser.list("video/2026-07-03");

        assertEquals(List.of("video", "SOS", "image", "audio"),
                roots.stream().map(MediaEntry::getName).toList());
        assertEquals("clip.mp4", files.get(0).getName());
        assertEquals("video/mp4", files.get(0).getMimeType());
    }

    @Test void rejectsPathsOutsideMediaRoots() throws Exception {
        Path root = Files.createTempDirectory("dcam-media");
        LocalMediaRepositoryImpl browser = new LocalMediaRepositoryImpl(new DcamStorage(root.toFile()));
        assertThrows(SecurityException.class, () -> browser.list("../private"));
        assertThrows(SecurityException.class, () -> browser.list("log"));
    }
}
