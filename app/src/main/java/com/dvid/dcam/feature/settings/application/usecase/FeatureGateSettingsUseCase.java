package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.domain.FeatureGate;

/** Application entry point for project-phase feature gates. */
public interface FeatureGateSettingsUseCase {
    boolean isEnabled(FeatureGate feature);
    void setEnabled(FeatureGate feature, boolean enabled);
}
