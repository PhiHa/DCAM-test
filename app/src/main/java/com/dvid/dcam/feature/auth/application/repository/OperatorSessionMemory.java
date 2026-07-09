package com.dvid.dcam.feature.auth.application.repository;

import com.dvid.dcam.feature.auth.domain.OperatorSession;

/** Process-local session cache used by synchronous capture and hardware-button guards. */
public final class OperatorSessionMemory {
    private volatile OperatorSession current;

    public OperatorSession current() { return current; }
    public boolean hasActiveSession() { return current != null; }
    public void set(OperatorSession session) { current = session; }
    public void clear() { current = null; }
}
