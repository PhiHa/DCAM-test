package com.dvid.dcam.core.config.application.port;

import com.dvid.dcam.core.config.domain.DcamConfig;

/** Resolves safe configuration without exposing source details to callers. */
public interface ConfigurationRepository {
    DcamConfig load(String defaultAccountUserId);
}
