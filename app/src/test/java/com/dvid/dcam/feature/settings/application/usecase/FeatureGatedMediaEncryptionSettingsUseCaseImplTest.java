package com.dvid.dcam.feature.settings.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dvid.dcam.feature.settings.domain.FeatureGate;
import org.junit.jupiter.api.Test;

final class FeatureGatedMediaEncryptionSettingsUseCaseImplTest {
    @Test void disabledSecurityFeatureForcesEncryptionOff() {
        FakeMediaEncryptionSettingsUseCaseImpl delegate = new FakeMediaEncryptionSettingsUseCaseImpl();
        FakeFeatureGateSettingsUseCaseImpl gates = new FakeFeatureGateSettingsUseCaseImpl();
        delegate.setMediaEncryptionEnabled(true);
        gates.setEnabled(FeatureGate.SECURITY_ENCRYPTION, false);

        MediaEncryptionSettingsUseCase useCase =
                new FeatureGatedMediaEncryptionSettingsUseCaseImpl(delegate, gates);

        assertFalse(useCase.isMediaEncryptionEnabled());
    }

    @Test void enabledSecurityFeatureUsesSavedEncryptionPreference() {
        FakeMediaEncryptionSettingsUseCaseImpl delegate = new FakeMediaEncryptionSettingsUseCaseImpl();
        FakeFeatureGateSettingsUseCaseImpl gates = new FakeFeatureGateSettingsUseCaseImpl();
        delegate.setMediaEncryptionEnabled(true);
        gates.setEnabled(FeatureGate.SECURITY_ENCRYPTION, true);

        MediaEncryptionSettingsUseCase useCase =
                new FeatureGatedMediaEncryptionSettingsUseCaseImpl(delegate, gates);

        assertTrue(useCase.isMediaEncryptionEnabled());
    }

    private static final class FakeMediaEncryptionSettingsUseCaseImpl implements MediaEncryptionSettingsUseCase {
        private boolean enabled;

        @Override public boolean isMediaEncryptionEnabled() { return enabled; }
        @Override public void setMediaEncryptionEnabled(boolean enabled) { this.enabled = enabled; }
    }

    private static final class FakeFeatureGateSettingsUseCaseImpl implements FeatureGateSettingsUseCase {
        private boolean securityEnabled = true;

        @Override public boolean isEnabled(FeatureGate feature) {
            return feature != FeatureGate.SECURITY_ENCRYPTION || securityEnabled;
        }

        @Override public void setEnabled(FeatureGate feature, boolean enabled) {
            if (feature == FeatureGate.SECURITY_ENCRYPTION) securityEnabled = enabled;
        }
    }
}
