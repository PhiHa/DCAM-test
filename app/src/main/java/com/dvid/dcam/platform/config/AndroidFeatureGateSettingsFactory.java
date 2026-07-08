package com.dvid.dcam.platform.config;

import android.content.Context;
import com.dvid.dcam.feature.settings.application.usecase.FeatureGateSettingsUseCase;
import com.dvid.dcam.feature.settings.application.usecase.FeatureGateSettingsUseCaseImpl;
import com.dvid.dcam.feature.settings.domain.FeatureGate;

/** Android composition helper for SharedPreferences-backed feature gates. */
public final class AndroidFeatureGateSettingsFactory {
    private AndroidFeatureGateSettingsFactory() {}

    public static FeatureGateSettingsUseCase create(Context context) {
        return new FeatureGateSettingsUseCaseImpl(new AndroidFeatureGateStoreImpl(context));
    }

    public static boolean isEnabled(Context context, FeatureGate feature) {
        return create(context).isEnabled(feature);
    }
}
