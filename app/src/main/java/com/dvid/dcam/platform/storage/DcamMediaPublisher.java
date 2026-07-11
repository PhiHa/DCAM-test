package com.dvid.dcam.platform.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/** Copies, flushes and verifies staging before exposing a final media path. */
final class DcamMediaPublisher {
    private static final int COPY_BUFFER_BYTES = 64 * 1024;

    File publish(File staging, File target) throws IOException {
        validate(staging, "staging");
        File parent = target.getParentFile();
        if (parent == null || (!parent.isDirectory() && !parent.mkdirs())) {
            throw new IOException("Cannot create final media directory: " + parent);
        }
        if (target.exists()) throw new IOException("Final media already exists: " + target);

        File partial = new File(parent, "." + target.getName() + ".publishing-" + UUID.randomUUID());
        boolean targetCreated = false;
        try {
            copyAndSync(staging, partial);
            validate(partial, "publication copy");
            if (partial.length() != staging.length()) {
                throw new IOException("Publication copy size mismatch");
            }
            try {
                Files.move(partial.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(partial.toPath(), target.toPath());
            }
            targetCreated = true;
            validate(target, "final media");
            return target;
        } catch (IOException | RuntimeException failure) {
            try { Files.deleteIfExists(partial.toPath()); } catch (IOException cleanup) {
                failure.addSuppressed(cleanup);
            }
            if (targetCreated) {
                try { Files.deleteIfExists(target.toPath()); } catch (IOException cleanup) {
                    failure.addSuppressed(cleanup);
                }
            }
            throw failure;
        }
    }

    static void validate(File file, String label) throws IOException {
        if (!file.isFile() || !file.canRead() || file.length() <= 0L) {
            throw new IOException("Invalid " + label + ": " + file);
        }
    }

    private static void copyAndSync(File source, File target) throws IOException {
        try (FileInputStream input = new FileInputStream(source);
             FileOutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[COPY_BUFFER_BYTES];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                output.write(buffer, 0, read);
            }
            output.flush();
            output.getFD().sync();
        }
    }
}
