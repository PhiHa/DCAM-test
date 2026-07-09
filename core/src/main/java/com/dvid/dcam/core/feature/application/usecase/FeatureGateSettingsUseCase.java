package com.dvid.dcam.core.feature.application.usecase;

import com.dvid.dcam.core.feature.domain.FeatureGate;

/** Application entry point for opt-in developer feature gates. */
public interface FeatureGateSettingsUseCase {
    boolean isEnabled(FeatureGate feature);
    void setEnabled(FeatureGate feature, boolean enabled);

    /** Central guard for non-UI entry points such as hardware commands. */
    default boolean runIfEnabled(FeatureGate feature, Runnable action) {
        if (!isEnabled(feature)) return false;
        action.run();
        return true;
    }
}
