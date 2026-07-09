package com.dvid.dcam.feature.auth.application.usecase;

import com.dvid.dcam.feature.auth.application.port.BootIdentitySource;
import com.dvid.dcam.feature.auth.application.port.OperatorAuthRepository;
import com.dvid.dcam.feature.auth.application.repository.OperatorSessionMemory;
import com.dvid.dcam.feature.auth.domain.OperatorSession;

public final class OperatorSessionUseCaseImpl implements OperatorSessionUseCase {
    private final OperatorAuthRepository repository;
    private final BootIdentitySource bootIdentity;
    private final OperatorSessionMemory memory;

    public OperatorSessionUseCaseImpl(
            OperatorAuthRepository repository,
            BootIdentitySource bootIdentity,
            OperatorSessionMemory memory) {
        this.repository = repository;
        this.bootIdentity = bootIdentity;
        this.memory = memory;
    }

    @Override
    public OperatorSession restore() {
        OperatorSession restored = repository.activeSessionForBoot(
                bootIdentity.currentBootId(), System.currentTimeMillis());
        if (restored == null) memory.clear(); else memory.set(restored);
        return restored;
    }

    @Override
    public OperatorSession current() {
        return memory.current();
    }

    @Override
    public boolean hasActiveSession() {
        return memory.hasActiveSession();
    }

    @Override
    public void logout() {
        repository.endActiveSession(System.currentTimeMillis(), "LOGOUT");
        memory.clear();
    }
}
