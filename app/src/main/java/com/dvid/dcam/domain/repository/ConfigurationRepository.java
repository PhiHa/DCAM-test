package com.dvid.dcam.domain.repository;

import com.dvid.dcam.domain.model.DcamConfig;

/** Resolves safe local/default configuration without exposing file details to UI. */
public interface ConfigurationRepository {
    DcamConfig load(String defaultAccountUserId);
}
