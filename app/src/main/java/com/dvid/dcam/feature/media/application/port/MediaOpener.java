package com.dvid.dcam.feature.media.application.port;

/** Opens a managed media item through the current platform. */
public interface MediaOpener {
    boolean open(String relativePath, String mimeType);
}
