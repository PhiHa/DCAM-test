package com.dvid.dcam.core.config.application.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.dvid.dcam.core.config.application.port.ConfigurationSource;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.core.logging.application.port.LogSink;
import org.junit.jupiter.api.Test;

final class ConfigurationRepositoryImplTest {
    @Test void returnsConfigurationLoadedBySource() {
        DcamConfig expected = new DcamConfig("account", "operator", false, "password");
        ConfigurationRepositoryImpl repository =
                new ConfigurationRepositoryImpl(defaultAccount -> expected, new RecordingLogSink());

        assertSame(expected, repository.load("fallback"));
    }

    @Test void fallsBackToDefaultsAndLogsSourceFailure() {
        RuntimeException failure = new RuntimeException("broken config");
        ConfigurationSource source = defaultAccount -> {
            throw failure;
        };
        RecordingLogSink log = new RecordingLogSink();
        ConfigurationRepositoryImpl repository = new ConfigurationRepositoryImpl(source, log);

        DcamConfig result = repository.load("fallback");

        assertEquals("fallback", result.getAccountUserId());
        assertEquals("Using default config", log.warning);
        assertSame(failure, log.error);
    }

    private static final class RecordingLogSink implements LogSink {
        private String warning;
        private Throwable error;

        @Override public void info(String message) {}

        @Override public void warn(String message, Throwable error) {
            warning = message;
            this.error = error;
        }

        @Override public void error(String message, Throwable error) {}
    }
}
