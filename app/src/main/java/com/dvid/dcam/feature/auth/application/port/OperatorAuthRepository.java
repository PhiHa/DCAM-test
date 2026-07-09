package com.dvid.dcam.feature.auth.application.port;

import com.dvid.dcam.feature.auth.domain.OperatorAccount;
import com.dvid.dcam.feature.auth.domain.OperatorSession;
import java.util.List;

/** Persistence boundary for accounts, credentials, and the single active operator session. */
public interface OperatorAuthRepository {
    void insertIfMissing(OperatorAccount account, String passwordText);
    void upsert(OperatorAccount account, String passwordText);
    List<OperatorAccount> findCredentialMatches(String normalizedIdentifier, String passwordText);
    OperatorSession replaceActiveSession(OperatorAccount account, String bootId, long nowEpochMillis);
    OperatorSession activeSessionForBoot(String bootId, long nowEpochMillis);
    void endActiveSession(long nowEpochMillis, String reason);
    List<OperatorAccount> listUsers();
}
