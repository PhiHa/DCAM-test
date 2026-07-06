package com.dvid.dcam.core.config;


/** Resolves safe local/default configuration without exposing file details to UI. */
public interface ConfigurationRepository {
    DcamConfig load(String defaultAccountUserId);
}
