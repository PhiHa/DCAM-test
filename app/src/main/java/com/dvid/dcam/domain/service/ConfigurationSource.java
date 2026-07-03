package com.dvid.dcam.domain.service;

import com.dvid.dcam.domain.model.DcamConfig;

/** Local/remote/default configuration source boundary used by the repository. */
public interface ConfigurationSource {
    DcamConfig load(String defaultAccountUserId) throws Exception;
}
