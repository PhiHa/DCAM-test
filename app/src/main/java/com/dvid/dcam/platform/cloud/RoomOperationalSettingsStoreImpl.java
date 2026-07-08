package com.dvid.dcam.platform.cloud;

import com.dvid.dcam.feature.cloud.application.port.OperationalSettingsStore;
import com.dvid.dcam.platform.database.dao.CloudStateDao;
import com.dvid.dcam.platform.database.entities.OperationalSettingEntity;

/** Room-backed store for operational settings that must not live in device CSON. */
public final class RoomOperationalSettingsStoreImpl implements OperationalSettingsStore {
    private final CloudStateDao dao;

    public RoomOperationalSettingsStoreImpl(CloudStateDao dao) {
        this.dao = dao;
    }

    @Override public String get(String key) {
        return dao.operationalSetting(key);
    }

    @Override public void put(String key, String value) {
        if (key == null || key.trim().isEmpty()) throw new IllegalArgumentException("key is required");
        dao.saveOperationalSetting(new OperationalSettingEntity(key.trim(), value, System.currentTimeMillis()));
    }
}
