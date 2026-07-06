package com.dvid.dcam.platform.storage;

import com.dvid.dcam.feature.media.domain.MediaBrowserService;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Sandboxed read-only browser for the four DCAM media roots. */
public final class LocalMediaBrowserService implements MediaBrowserService {
    private final File root;
    private final Set<String> allowedRoots = new LinkedHashSet<>();

    public LocalMediaBrowserService(DcamStorage storage) {
        root = storage.rootDirectory();
        for (DcamFileType type : DcamFileType.values()) allowedRoots.add(type.getFolder());
    }

    @Override public List<MediaEntry> list(String relativePath) throws Exception {
        String safePath = normalize(relativePath);
        if (safePath.isEmpty()) return mediaRoots();
        ensureAllowedTopLevel(safePath);
        File directory = resolveInsideRoot(safePath);
        if (!directory.exists() || !directory.isDirectory()) return Collections.emptyList();
        File[] children = directory.listFiles();
        if (children == null) return Collections.emptyList();
        Arrays.sort(children, Comparator.comparing(File::isFile).thenComparing(
                file -> file.getName().toLowerCase(Locale.ROOT)));
        List<MediaEntry> entries = new ArrayList<>();
        for (File child : children) {
            String childRelative = safePath + "/" + child.getName();
            entries.add(entry(child, childRelative));
        }
        return entries;
    }

    private List<MediaEntry> mediaRoots() throws Exception {
        List<MediaEntry> entries = new ArrayList<>();
        for (String folder : allowedRoots) {
            File file = resolveInsideRoot(folder);
            entries.add(new MediaEntry(folder, folder, file.getAbsolutePath(), true,
                    0L, file.exists() ? file.lastModified() : 0L, null));
        }
        return entries;
    }

    private MediaEntry entry(File file, String relativePath) {
        return new MediaEntry(file.getName(), relativePath.replace('\\', '/'),
                file.getAbsolutePath(), file.isDirectory(), file.isFile() ? file.length() : 0L,
                file.lastModified(), file.isDirectory() ? null : mimeType(file.getName()));
    }

    private File resolveInsideRoot(String relativePath) throws Exception {
        File canonicalRoot = root.getCanonicalFile();
        File candidate = new File(canonicalRoot, relativePath).getCanonicalFile();
        String rootPath = canonicalRoot.getPath() + File.separator;
        if (!candidate.getPath().startsWith(rootPath)) {
            throw new SecurityException("Media path escapes DCAM root");
        }
        return candidate;
    }

    private void ensureAllowedTopLevel(String relativePath) {
        String top = relativePath.contains("/")
                ? relativePath.substring(0, relativePath.indexOf('/')) : relativePath;
        if (!allowedRoots.contains(top)) throw new SecurityException("Unsupported media folder");
    }

    private static String normalize(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return "";
        String normalized = relativePath.replace('\\', '/');
        while (normalized.startsWith("/")) normalized = normalized.substring(1);
        while (normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        return normalized;
    }

    private static String mimeType(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".m4a")) return "audio/mp4";
        if (lower.endsWith(".mp3")) return "audio/mpeg";
        if (lower.endsWith(".wav")) return "audio/wav";
        return "application/octet-stream";
    }
}
