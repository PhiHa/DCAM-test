package com.dvid.dcam.feature.auth.application.port;

/** Supplies a stable identifier for the current device boot. */
public interface BootIdentitySource {
    String currentBootId();
}
