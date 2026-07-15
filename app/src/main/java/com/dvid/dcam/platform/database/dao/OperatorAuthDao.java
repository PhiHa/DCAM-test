package com.dvid.dcam.platform.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.dvid.dcam.platform.database.entities.OperatorSessionEntity;
import com.dvid.dcam.platform.database.entities.UserAuthMethodEntity;
import com.dvid.dcam.platform.database.entities.UserProfileEntity;
import java.util.List;

@Dao
public abstract class OperatorAuthDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertUser(UserProfileEntity user);

    @Update
    public abstract void updateUser(UserProfileEntity user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertAuthMethod(UserAuthMethodEntity method);

    @Update
    public abstract void updateAuthMethod(UserAuthMethodEntity method);

    @Query("SELECT created_at FROM user_profile WHERE user_id = :userId")
    public abstract Long userCreatedAt(String userId);

    @Query("SELECT created_at FROM user_auth_method WHERE auth_method_id = :methodId")
    public abstract Long authMethodCreatedAt(String methodId);

    @Query("SELECT a.* FROM user_auth_method a "
            + "JOIN user_profile u ON u.user_id = a.user_id "
            + "WHERE u.status = 'ACTIVE' AND a.status = 'ACTIVE' "
            + "AND a.method_type = 'PASSWORD_HASH' "
            + "AND (:identifier IS NULL OR LOWER(u.user_id) = :identifier) "
            + "ORDER BY u.user_id")
    public abstract List<UserAuthMethodEntity> credentialCandidates(String identifier);

    @Query("SELECT * FROM user_profile "
            + "WHERE user_id = :userId AND status = 'ACTIVE' LIMIT 1")
    public abstract UserProfileEntity activeUser(String userId);

    @Query("SELECT * FROM user_profile ORDER BY user_id")
    public abstract List<UserProfileEntity> users();

    @Query("UPDATE operator_session SET status = 'ENDED', active_slot = NULL, "
            + "ended_at = :now, end_reason = :reason, revision = revision + 1 "
            + "WHERE active_slot = 1")
    public abstract void endActiveSession(long now, String reason);

    @Query("UPDATE operator_session SET status = 'ENDED', active_slot = NULL, "
            + "ended_at = :now, end_reason = 'DEVICE_REBOOT', revision = revision + 1 "
            + "WHERE active_slot = 1 AND boot_id != :bootId")
    public abstract void expireSessionsOutsideBoot(String bootId, long now);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insertSession(OperatorSessionEntity session);

    @Query("SELECT * FROM operator_session "
            + "WHERE active_slot = 1 AND status = 'ACTIVE' AND boot_id = :bootId LIMIT 1")
    public abstract OperatorSessionEntity activeSession(String bootId);

    @Transaction
    public void insertIfMissing(UserProfileEntity user, UserAuthMethodEntity method) {
        insertUser(user);
        insertAuthMethod(method);
    }

    @Transaction
    public void saveUser(UserProfileEntity user, UserAuthMethodEntity method) {
        Long userCreatedAt = userCreatedAt(user.userId);
        if (userCreatedAt == null) {
            insertUser(user);
        } else {
            user.createdAt = userCreatedAt;
            updateUser(user);
        }
        Long methodCreatedAt = authMethodCreatedAt(method.authMethodId);
        if (methodCreatedAt == null) {
            insertAuthMethod(method);
        } else {
            method.createdAt = methodCreatedAt;
            updateAuthMethod(method);
        }
    }

    @Transaction
    public void replaceActiveSession(OperatorSessionEntity session) {
        endActiveSession(session.startedAt, "REPLACED_BY_LOGIN");
        insertSession(session);
    }

    @Transaction
    public OperatorSessionEntity restoreActiveSession(String bootId, long now) {
        expireSessionsOutsideBoot(bootId, now);
        return activeSession(bootId);
    }
}
