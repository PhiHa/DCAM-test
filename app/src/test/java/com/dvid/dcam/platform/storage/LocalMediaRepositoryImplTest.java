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
}
