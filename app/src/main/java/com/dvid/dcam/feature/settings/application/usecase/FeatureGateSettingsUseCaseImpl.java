package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.application.port.FeatureGateStore;
import com.dvid.dcam.feature.settings.domain.FeatureGate;

public final class FeatureGateSettingsUseCaseImpl implements FeatureGateSettingsUseCase {
    private final FeatureGateStore store;

    public FeatureGateSettingsUseCaseImpl(FeatureGateStore store) {
        this.store = store;
    }

    @Override public boolean isEnabled(FeatureGate feature) {
        return store.isEnabled(feature);
    }

    @Override public void setEnabled(FeatureGate feature, boolean enabled) {
        store.setEnabled(feature, enabled);
    }
}
