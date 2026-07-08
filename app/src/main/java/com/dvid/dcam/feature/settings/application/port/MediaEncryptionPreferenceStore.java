package com.dvid.dcam.feature.settings.application.port;

/** Persistence boundary for the runtime media encryption setting. */
public interface MediaEncryptionPreferenceStore {
    boolean isMediaEncryptionEnabled();
    void setMediaEncryptionEnabled(boolean enabled);
}
