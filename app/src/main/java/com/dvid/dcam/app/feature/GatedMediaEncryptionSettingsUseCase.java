package com.dvid.dcam.app.feature;

import com.dvid.dcam.feature.settings.application.usecase.MediaEncryptionSettingsUseCase;
import java.util.function.BooleanSupplier;

public final class GatedMediaEncryptionSettingsUseCase implements MediaEncryptionSettingsUseCase {
    private final MediaEncryptionSettingsUseCase delegate;
    private final BooleanSupplier enabled;

    public GatedMediaEncryptionSettingsUseCase(
            MediaEncryptionSettingsUseCase delegate, BooleanSupplier enabled) {
        this.delegate = delegate;
        this.enabled = enabled;
    }

    @Override public boolean isMediaEncryptionEnabled() {
        return enabled.getAsBoolean() && delegate.isMediaEncryptionEnabled();
    }

    @Override public void setMediaEncryptionEnabled(boolean enabled) {
        delegate.setMediaEncryptionEnabled(enabled);
    }
}
