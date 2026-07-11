package com.dvid.dcam.feature.settings.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.dvid.dcam.feature.settings.application.port.StorageModePreferenceStore;
import com.dvid.dcam.feature.settings.domain.StorageMode;
import org.junit.jupiter.api.Test;

final class StorageSettingsUseCaseImplTest {
    @Test void readsAndChangesOperatorStorageSelection() {
        FakeStorageModePreferenceStore preferences = new FakeStorageModePreferenceStore();
        StorageSettingsUseCase settings = new StorageSettingsUseCaseImpl(preferences);

        assertEquals(StorageMode.AUTO, settings.currentMode());
        settings.changeMode(StorageMode.INTERNAL);
        assertEquals(StorageMode.INTERNAL, settings.currentMode());
    }

    private static final class FakeStorageModePreferenceStore implements StorageModePreferenceStore {
        private StorageMode mode = StorageMode.AUTO;

        @Override public StorageMode currentStorageMode() { return mode; }

        @Override public void selectStorageMode(StorageMode mode) { this.mode = mode; }
    }
}
