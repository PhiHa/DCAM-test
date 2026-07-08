package com.dvid.dcam.feature.settings.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.dvid.dcam.feature.settings.domain.FeatureGate;
import org.junit.jupiter.api.Test;

final class FeatureGateSettingsScreenModelTest {
    @Test void featureControlsMapToFeatureGatesOutsideActivity() {
        FeatureGateSettingsScreenModel model = new FeatureGateSettingsScreenModel();

        assertEquals(FeatureGate.CLOUD_NETWORK, model.featureFor(SettingId.FEATURE_CLOUD_NETWORK));
        assertEquals(FeatureGate.IMAGE_CAPTURE, model.featureFor(SettingId.FEATURE_IMAGE_CAPTURE));
    }

    @Test void nonFeatureControlsAreIgnoredByFeatureGateModel() {
        FeatureGateSettingsScreenModel model = new FeatureGateSettingsScreenModel();

        assertNull(model.featureFor(SettingId.LANGUAGE));
    }
}
