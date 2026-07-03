package com.dvid.dcam.data.repository;

import com.dvid.dcam.domain.model.DcamConfig;
import com.dvid.dcam.domain.repository.ConfigurationRepository;
import com.dvid.dcam.domain.service.ConfigurationSource;
import com.dvid.dcam.domain.service.LogService;

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
