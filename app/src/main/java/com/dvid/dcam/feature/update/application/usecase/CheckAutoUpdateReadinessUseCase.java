package com.dvid.dcam.feature.update.application.usecase;

import com.dvid.dcam.feature.update.domain.AutoUpdatePreconditions;

public interface CheckAutoUpdateReadinessUseCase {
    boolean canCheckForUpdate(AutoUpdatePreconditions preconditions);
}
