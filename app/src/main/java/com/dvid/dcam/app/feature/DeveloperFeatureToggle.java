package com.dvid.dcam.app.feature;

import com.dvid.dcam.core.feature.domain.FeatureGate;
import com.dvid.dcam.feature.settings.presentation.SettingId;
import java.util.Objects;

/** Developer-mode metadata for one feature that explicitly supports runtime disablement. */
public final class DeveloperFeatureToggle {
    private final FeatureGate gate;
    private final String developerSection;
    private final String developerLabel;
    private final SettingId developerSetting;
    private final FeatureGate parentGate;

    DeveloperFeatureToggle(
            FeatureGate gate,
            String developerSection,
            String developerLabel,
            SettingId developerSetting,
            FeatureGate parentGate) {
        this.gate = Objects.requireNonNull(gate);
        this.developerSection = Objects.requireNonNull(developerSection);
        this.developerLabel = Objects.requireNonNull(developerLabel);
        this.developerSetting = Objects.requireNonNull(developerSetting);
        this.parentGate = parentGate;
    }

    public FeatureGate getGate() { return gate; }
    public String getDeveloperSection() { return developerSection; }
    public String getDeveloperLabel() { return developerLabel; }
    public SettingId getDeveloperSetting() { return developerSetting; }
    public FeatureGate getParentGate() { return parentGate; }
}
