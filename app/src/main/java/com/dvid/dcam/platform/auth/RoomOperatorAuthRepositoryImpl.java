package com.dvid.dcam.platform.auth;

import com.dvid.dcam.feature.auth.application.port.OperatorAuthRepository;
import com.dvid.dcam.feature.auth.domain.OperatorAccount;
import com.dvid.dcam.feature.auth.domain.OperatorSession;
import com.dvid.dcam.feature.auth.domain.UserSource;
import com.dvid.dcam.platform.database.dao.OperatorAuthDao;
import com.dvid.dcam.platform.database.entities.OperatorSessionEntity;
import com.dvid.dcam.platform.database.entities.UserAuthMethodEntity;
import com.dvid.dcam.platform.database.entities.UserProfileEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RoomOperatorAuthRepositoryImpl implements OperatorAuthRepository {
    private static final String PASSWORD_HASH = "PASSWORD_HASH";
    private final OperatorAuthDao dao;
    private final BcryptPasswordHasher passwordHasher;

    public RoomOperatorAuthRepositoryImpl(OperatorAuthDao dao) {
        this(dao, new BcryptPasswordHasher());
    }

    RoomOperatorAuthRepositoryImpl(
            OperatorAuthDao dao,
            BcryptPasswordHasher passwordHasher) {
        this.dao = dao;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void insertIfMissing(OperatorAccount account, String passwordText) {
        String methodId = passwordMethodId(account.getUserId());
        if (dao.userCreatedAt(account.getUserId()) != null
                && dao.authMethodCreatedAt(methodId) != null) return;
        long now = System.currentTimeMillis();
        dao.insertIfMissing(toUser(account, now), toMethod(account, passwordText, now));
    }

    @Override
    public void upsert(OperatorAccount account, String passwordText) {
        long now = System.currentTimeMillis();
        dao.saveUser(toUser(account, now), toMethod(account, passwordText, now));
    }

    @Override
    public List<OperatorAccount> findCredentialMatches(
            String normalizedIdentifier, String passwordText) {
        List<OperatorAccount> matches = new ArrayList<>(2);
        for (UserAuthMethodEntity candidate : dao.credentialCandidates(normalizedIdentifier)) {
            if (!passwordHasher.matches(passwordText, candidate)) continue;
            if (passwordHasher.needsRehash(candidate)) {
                PasswordCredential replacement = passwordHasher.hash(passwordText);
                candidate.credentialAlgorithm = replacement.algorithm();
                candidate.credentialSalt = replacement.salt();
                candidate.credentialHash = replacement.hash();
                candidate.credentialIterations = replacement.iterations();
                candidate.updatedAt = System.currentTimeMillis();
                candidate.revision++;
                dao.updateAuthMethod(candidate);
            }
            UserProfileEntity user = dao.activeUser(candidate.userId);
            if (user != null) matches.add(toAccount(user));
            if (matches.size() == 2) break;
        }
        return matches;
    }

    @Override
    public OperatorSession replaceActiveSession(
            OperatorAccount account, String bootId, long nowEpochMillis) {
        OperatorSessionEntity entity = new OperatorSessionEntity(
                UUID.randomUUID().toString(),
                account.getUserId(),
                account.getFileUserId(),
                account.getDisplayName(),
                bootId,
                "ACTIVE",
                1,
                nowEpochMillis,
                null,
                null,
                1);
        dao.replaceActiveSession(entity);
        return toSession(entity);
    }

    @Override
    public OperatorSession activeSessionForBoot(String bootId, long nowEpochMillis) {
        OperatorSessionEntity entity = dao.restoreActiveSession(bootId, nowEpochMillis);
        return entity == null ? null : toSession(entity);
    }

    @Override
    public void endActiveSession(long nowEpochMillis, String reason) {
        dao.endActiveSession(nowEpochMillis, reason);
    }

    @Override
    public List<OperatorAccount> listUsers() {
        return toAccounts(dao.users());
    }

    private static UserProfileEntity toUser(OperatorAccount account, long now) {
        return new UserProfileEntity(
                account.getUserId(),
                account.getFileUserId(),
                account.getLoginName(),
                account.getDisplayName(),
                account.isActive() ? "ACTIVE" : "DISABLED",
                account.getSource().name(),
                now,
                now,
                1);
    }

    private UserAuthMethodEntity toMethod(
            OperatorAccount account, String passwordText, long now) {
        PasswordCredential credential = passwordHasher.hash(passwordText);
        return new UserAuthMethodEntity(
                "password:" + account.getUserId(),
                account.getUserId(),
                PASSWORD_HASH,
                credential.algorithm(),
                credential.salt(),
                credential.hash(),
                credential.iterations(),
                "ACTIVE",
                now,
                now,
                1);
    }

    private static String passwordMethodId(String userId) {
        return "password:" + userId;
    }

    private static List<OperatorAccount> toAccounts(List<UserProfileEntity> entities) {
        List<OperatorAccount> accounts = new ArrayList<>(entities.size());
        for (UserProfileEntity entity : entities) accounts.add(toAccount(entity));
        return accounts;
    }

    private static OperatorAccount toAccount(UserProfileEntity entity) {
        UserSource source;
        try {
            source = UserSource.valueOf(entity.source);
        } catch (RuntimeException ignored) {
            source = UserSource.CLOUD;
        }
        return new OperatorAccount(
                entity.userId,
                entity.fileUserId,
                entity.loginName,
                entity.displayName,
                source,
                "ACTIVE".equals(entity.status));
    }

    private static OperatorSession toSession(OperatorSessionEntity entity) {
        return new OperatorSession(
                entity.sessionId,
                entity.userId,
                entity.fileUserIdSnapshot,
                entity.displayNameSnapshot,
                entity.bootId,
                entity.startedAt);
    }
}
