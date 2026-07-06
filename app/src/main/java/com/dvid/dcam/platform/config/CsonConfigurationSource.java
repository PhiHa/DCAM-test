package com.dvid.dcam.platform.config;

import com.dvid.dcam.core.config.ConfigurationSource;
import com.dvid.dcam.core.config.DcamConfig;
import com.dvid.dcam.platform.storage.DcamStorage;

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
