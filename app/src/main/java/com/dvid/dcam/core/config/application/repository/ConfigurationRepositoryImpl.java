package com.dvid.dcam.core.config.application.repository;

import com.dvid.dcam.core.config.application.port.ConfigurationRepository;
import com.dvid.dcam.core.config.application.port.ConfigurationSource;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.core.logging.application.port.LogSink;

public final class ConfigurationRepositoryImpl implements ConfigurationRepository {
    private final ConfigurationSource source;
    private final LogSink log;

    public ConfigurationRepositoryImpl(ConfigurationSource source, LogSink log) {
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
