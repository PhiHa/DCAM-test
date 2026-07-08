package com.dvid.dcam.feature.cloud.application.port;

import com.dvid.dcam.feature.cloud.domain.RemoteConfigSnapshot;

/** Local cache for remote config revisions and apply results. */
public interface RemoteConfigStore {
    RemoteConfigSnapshot cached();
    void save(RemoteConfigSnapshot snapshot);
}
