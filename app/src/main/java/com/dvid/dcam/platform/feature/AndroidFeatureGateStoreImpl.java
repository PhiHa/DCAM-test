package com.dvid.dcam.platform.feature;

import android.content.Context;
import android.content.SharedPreferences;
import com.dvid.dcam.core.feature.application.port.FeatureGateStore;
import com.dvid.dcam.core.feature.domain.FeatureGate;

/** Android SharedPreferences storage for features explicitly made developer-disableable. */
public final class AndroidFeatureGateStoreImpl implements FeatureGateStore {
    private static final String PREFS_NAME = "dcam_feature_gates";

    private final Context context;

    public AndroidFeatureGateStoreImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override public boolean isEnabled(FeatureGate feature) {
        return prefs().getBoolean(feature.persistedKey(), feature.defaultEnabled());
    }

    @Override public void setEnabled(FeatureGate feature, boolean enabled) {
        prefs().edit().putBoolean(feature.persistedKey(), enabled).apply();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
