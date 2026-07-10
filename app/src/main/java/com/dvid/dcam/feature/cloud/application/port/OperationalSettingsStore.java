package com.dvid.dcam.feature.cloud.application.port;

/** DB-backed operational settings boundary. */
public interface OperationalSettingsStore {
    String get(String key);
    void put(String key, String value);
}
