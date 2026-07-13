package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class DcamMediaFinalizerTest {
    @TempDir Path root;

    @Test
    void flushesVerifiesAndPublishesBeforeRemovingStaging() throws Exception {
        DcamStorage storage = new DcamStorage(root.toFile());
        DcamMediaFile media = staged(storage, false);
        byte[] bytes = new byte[] {1, 2, 3, 4};
        Files.write(media.getFile().toPath(), bytes);

        java.io.File published = new DcamMediaFinalizer(storage).finalizeMedia(media);

        assertTrue(published.isFile());
        assertArrayEquals(bytes, Files.readAllBytes(published.toPath()));
        assertFalse(media.getFile().exists());
        assertFalse(published.getName().startsWith("."));
    }

    @Test
    void publicationConflictPreservesStagingAndExistingFinal() throws Exception {
        DcamStorage storage = new DcamStorage(root.toFile());
        DcamMediaFile media = staged(storage, false);
        Files.write(media.getFile().toPath(), new byte[] {1, 2, 3});
        java.io.File target = storage.finalFile(media);
        Files.createDirectories(target.toPath().getParent());
        Files.write(target.toPath(), new byte[] {9});

        assertThrows(IOException.class,
                () -> new DcamMediaFinalizer(storage).finalizeMedia(media));

        assertTrue(media.getFile().isFile());
        assertArrayEquals(new byte[] {9}, Files.readAllBytes(target.toPath()));
    }

    @Test
    void createsSameBasenameMd5ForVideoWhenEnabled() throws Exception {
        DcamStorage storage = new DcamStorage(root.toFile());
        DcamMediaFile media = staged(storage, false);
        Files.writeString(media.getFile().toPath(), "abc");

        java.io.File published = new DcamMediaFinalizer(storage).finalizeMedia(media, true);
        Path sidecar = published.toPath().resolveSibling(
                published.getName().replaceFirst("\\.mp4$", ".md5"));

        assertEquals("900150983cd24fb0d6963f7d28e17f72",
                Files.readString(sidecar).trim());
    }

    @Test
    void invalidOrEmptyStagingIsNeverPublished() throws Exception {
        DcamStorage storage = new DcamStorage(root.toFile());
        DcamMediaFile media = staged(storage, false);
        Files.createFile(media.getFile().toPath());

        assertThrows(IOException.class,
                () -> new DcamMediaFinalizer(storage).finalizeMedia(media));

        assertTrue(media.getFile().exists());
        assertFalse(storage.finalFile(media).exists());
    }

    private static DcamMediaFile staged(DcamStorage storage, boolean encrypted) throws IOException {
        DcamMediaFile media = storage.mediaFile(
                DcamFileType.VIDEO, "CAM001", "000001",
                LocalDateTime.of(2026, 7, 11, 10, 30), encrypted);
        Files.createDirectories(media.getFile().toPath().getParent());
        return media;
    }
}
