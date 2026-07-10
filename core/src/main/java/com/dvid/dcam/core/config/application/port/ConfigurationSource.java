package com.dvid.dcam.core.config.application.port;

import com.dvid.dcam.core.config.domain.DcamConfig;

/** Configuration-source capability used by the repository implementation. */
public interface ConfigurationSource {
    DcamConfig load(String defaultAccountUserId) throws Exception;
}
