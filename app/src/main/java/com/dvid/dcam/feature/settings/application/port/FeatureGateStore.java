package com.dvid.dcam.feature.settings.application.port;

import com.dvid.dcam.feature.settings.domain.FeatureGate;

/** Persistence boundary for project-phase feature gates. */
public interface FeatureGateStore {
    boolean isEnabled(FeatureGate feature);
    void setEnabled(FeatureGate feature, boolean enabled);
}
