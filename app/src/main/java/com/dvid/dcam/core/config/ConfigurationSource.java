package com.dvid.dcam.core.config;


/** Local/remote/default configuration source boundary used by the repository. */
public interface ConfigurationSource {
    DcamConfig load(String defaultAccountUserId) throws Exception;
}
