package com.dvid.dcam.platform.config;

import android.content.Context;
import android.content.SharedPreferences;
import com.dvid.dcam.feature.settings.application.port.FeatureGateStore;
import com.dvid.dcam.feature.settings.domain.FeatureGate;

/** Android SharedPreferences implementation of project-phase feature gates. */
public final class AndroidFeatureGateStoreImpl implements FeatureGateStore {
    private static final String PREFS_NAME = "dcam_feature_gates";

    private final Context context;

    public AndroidFeatureGateStoreImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override public boolean isEnabled(FeatureGate feature) {
        return prefs().getBoolean(feature.name(), feature.defaultEnabled());
    }

    @Override public void setEnabled(FeatureGate feature, boolean enabled) {
        prefs().edit().putBoolean(feature.name(), enabled).apply();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
