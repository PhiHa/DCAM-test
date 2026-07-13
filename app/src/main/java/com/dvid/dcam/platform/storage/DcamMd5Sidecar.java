package com.dvid.dcam.platform.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

final class DcamMd5Sidecar {
    private static final int BUFFER_BYTES = 64 * 1024;

    private DcamMd5Sidecar() {}

    static File write(File media) throws IOException {
        File sidecar = new File(media.getParentFile(), baseName(media.getName()) + ".md5");
        File partial = new File(media.getParentFile(), "." + sidecar.getName()
                + ".publishing-" + UUID.randomUUID());
        byte[] content = (digest(media) + System.lineSeparator()).getBytes(StandardCharsets.US_ASCII);
        try {
            try (FileOutputStream output = new FileOutputStream(partial)) {
                output.write(content);
                output.flush();
                output.getFD().sync();
            }
            Files.move(partial.toPath(), sidecar.toPath(), StandardCopyOption.ATOMIC_MOVE);
            return sidecar;
        } catch (java.nio.file.AtomicMoveNotSupportedException unsupported) {
            Files.move(partial.toPath(), sidecar.toPath());
            return sidecar;
        } finally {
            Files.deleteIfExists(partial.toPath());
        }
    }

    private static String digest(File media) throws IOException {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException impossible) {
            throw new IOException("MD5 unavailable", impossible);
        }
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(media))) {
            byte[] buffer = new byte[BUFFER_BYTES];
            int read;
            while ((read = input.read(buffer)) >= 0) md5.update(buffer, 0, read);
        }
        StringBuilder hex = new StringBuilder(32);
        for (byte value : md5.digest()) hex.append(String.format("%02x", value & 0xff));
        return hex.toString();
    }

    private static String baseName(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
