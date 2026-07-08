package com.dvid.dcam.platform.cloud;

import com.dvid.dcam.feature.cloud.application.port.RemoteConfigStore;
import com.dvid.dcam.feature.cloud.domain.RemoteConfigSnapshot;
import com.dvid.dcam.platform.database.dao.CloudStateDao;
import com.dvid.dcam.platform.database.entities.RemoteConfigEntity;

/** Room-backed cache for accepted/deferred/rejected remote-config revisions. */
public final class RoomRemoteConfigStoreImpl implements RemoteConfigStore {
    private static final int SINGLETON_ID = 1;

    private final CloudStateDao dao;

    public RoomRemoteConfigStoreImpl(CloudStateDao dao) {
        this.dao = dao;
    }

    @Override public RemoteConfigSnapshot cached() {
        RemoteConfigEntity entity = dao.remoteConfig();
        return entity == null ? RemoteConfigSnapshot.none() : toDomain(entity);
    }

    @Override public void save(RemoteConfigSnapshot snapshot) {
        if (snapshot == null) return;
        long now = System.currentTimeMillis();
        dao.saveRemoteConfig(new RemoteConfigEntity(
                SINGLETON_ID, snapshot.getRevision(), snapshot.getPayloadJson(),
                snapshot.getStatus().name(), snapshot.getLastError(), now,
                snapshot.getStatus() == RemoteConfigSnapshot.Status.ACCEPTED ? now : 0L));
    }

    private static RemoteConfigSnapshot toDomain(RemoteConfigEntity entity) {
        return new RemoteConfigSnapshot(
                entity.revision, entity.payloadJson, status(entity.status), entity.lastError);
    }

    private static RemoteConfigSnapshot.Status status(String value) {
        try {
            return RemoteConfigSnapshot.Status.valueOf(value);
        } catch (Exception ignored) {
            return RemoteConfigSnapshot.Status.NONE;
        }
    }
}
