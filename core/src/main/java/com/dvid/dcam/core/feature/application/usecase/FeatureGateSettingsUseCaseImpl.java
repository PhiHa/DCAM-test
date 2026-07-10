package com.dvid.dcam.core.feature.application.usecase;

import com.dvid.dcam.core.feature.application.port.FeatureGateStore;
import com.dvid.dcam.core.feature.domain.FeatureGate;

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
