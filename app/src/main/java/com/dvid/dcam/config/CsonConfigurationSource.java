package com.dvid.dcam.config;

import com.dvid.dcam.domain.model.DcamConfig;
import com.dvid.dcam.domain.service.ConfigurationSource;
import com.dvid.dcam.storage.DcamStorage;

/** Local CSON platform adapter. */
public final class CsonConfigurationSource implements ConfigurationSource {
    private final DcamStorage storage;

    public CsonConfigurationSource(DcamStorage storage) {
        this.storage = storage;
    }

    @Override public DcamConfig load(String defaultAccountUserId) throws Exception {
        return new CsonConfigStore(storage.configsFile(), defaultAccountUserId).load();
    }
}
