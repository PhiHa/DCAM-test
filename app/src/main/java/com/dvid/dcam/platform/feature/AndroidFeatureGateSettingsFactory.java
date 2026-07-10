package com.dvid.dcam.platform.feature;

import android.content.Context;
import com.dvid.dcam.core.feature.application.usecase.FeatureGateSettingsUseCase;
import com.dvid.dcam.core.feature.application.usecase.FeatureGateSettingsUseCaseImpl;

/** Android composition helper for SharedPreferences-backed feature gates. */
public final class AndroidFeatureGateSettingsFactory {
    private AndroidFeatureGateSettingsFactory() {}

    public static FeatureGateSettingsUseCase create(Context context) {
        return new FeatureGateSettingsUseCaseImpl(new AndroidFeatureGateStoreImpl(context));
    }
}
