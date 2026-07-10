package com.dvid.dcam.feature.auth.application.usecase;

import com.dvid.dcam.feature.auth.domain.OperatorSession;

public interface OperatorSessionUseCase {
    OperatorSession restore();
    OperatorSession current();
    boolean hasActiveSession();
    void logout();
}
