package com.dvid.dcam.feature.update.application.usecase;

import com.dvid.dcam.feature.update.domain.AutoUpdatePreconditions;

public final class CheckAutoUpdateReadinessUseCaseImpl implements CheckAutoUpdateReadinessUseCase {
    @Override public boolean canCheckForUpdate(AutoUpdatePreconditions preconditions) {
        return preconditions != null && preconditions.canCheckForUpdate();
    }
}
