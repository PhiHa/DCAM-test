package com.dvid.dcam.core.feature.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dvid.dcam.core.feature.application.port.FeatureGateStore;
import com.dvid.dcam.core.feature.domain.FeatureGate;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class FeatureGateSettingsUseCaseImplTest {
    @Test void delegatesReadsAndWritesToStore() {
        InMemoryFeatureGateStore store = new InMemoryFeatureGateStore();
        FeatureGateSettingsUseCase useCase = new FeatureGateSettingsUseCaseImpl(store);

        assertTrue(useCase.isEnabled(FeatureGate.AUDIO_CAPTURE));

        useCase.setEnabled(FeatureGate.AUDIO_CAPTURE, false);

        assertFalse(useCase.isEnabled(FeatureGate.AUDIO_CAPTURE));
    }

    private static final class InMemoryFeatureGateStore implements FeatureGateStore {
        private final Map<FeatureGate, Boolean> values = new EnumMap<>(FeatureGate.class);

        @Override public boolean isEnabled(FeatureGate feature) {
            return values.getOrDefault(feature, feature.defaultEnabled());
        }

        @Override public void setEnabled(FeatureGate feature, boolean enabled) {
            values.put(feature, enabled);
        }
    }
}
