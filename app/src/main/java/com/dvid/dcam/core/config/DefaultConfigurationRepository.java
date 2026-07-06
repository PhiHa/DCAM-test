package com.dvid.dcam.core.config;

import com.dvid.dcam.core.logging.LogService;

public final class DefaultConfigurationRepository implements ConfigurationRepository {
    private final ConfigurationSource source;
    private final LogService log;

    public DefaultConfigurationRepository(ConfigurationSource source, LogService log) {
        this.source = source;
        this.log = log;
    }

    @Override public DcamConfig load(String defaultAccountUserId) {
        try {
            return source.load(defaultAccountUserId);
        } catch (Exception error) {
            log.warn("Using default config", error);
            return DcamConfig.defaults(defaultAccountUserId);
        }
    }
}
