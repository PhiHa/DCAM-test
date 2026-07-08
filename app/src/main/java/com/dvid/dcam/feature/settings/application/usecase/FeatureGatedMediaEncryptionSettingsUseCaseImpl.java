package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.domain.FeatureGate;

public final class FeatureGatedMediaEncryptionSettingsUseCaseImpl implements MediaEncryptionSettingsUseCase {
    private final MediaEncryptionSettingsUseCase delegate;
    private final FeatureGateSettingsUseCase featureGates;

    public FeatureGatedMediaEncryptionSettingsUseCaseImpl(
            MediaEncryptionSettingsUseCase delegate,
            FeatureGateSettingsUseCase featureGates) {
        this.delegate = delegate;
        this.featureGates = featureGates;
    }

    @Override public boolean isMediaEncryptionEnabled() {
        return featureGates.isEnabled(FeatureGate.SECURITY_ENCRYPTION)
                && delegate.isMediaEncryptionEnabled();
    }

    @Override public void setMediaEncryptionEnabled(boolean enabled) {
        delegate.setMediaEncryptionEnabled(enabled);
    }
}
