package com.dvid.dcam.platform.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.dvid.dcam.platform.database.entities.PendingLogEntity;
import java.util.List;

@Dao
public interface PendingLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(PendingLogEntity event);

    @Query("SELECT * FROM pending_logs WHERE status = 'PENDING' ORDER BY id ASC LIMIT :limit")
    List<PendingLogEntity> oldestPending(int limit);

    @Query("SELECT * FROM pending_logs WHERE status = 'RETRY' AND nextAttemptAt <= :now "
            + "ORDER BY nextAttemptAt ASC, id ASC LIMIT :limit")
    List<PendingLogEntity> dueRetries(long now, int limit);

    @Query("SELECT MIN(nextAttemptAt) FROM pending_logs WHERE status = 'RETRY'")
    Long earliestRetryAt();

    @Query("SELECT * FROM pending_logs WHERE status = 'DEAD' ORDER BY id ASC")
    List<PendingLogEntity> deadEvents();

    @Query("DELETE FROM pending_logs WHERE id = :id")
    void delete(long id);

    @Query("SELECT COUNT(*) FROM pending_logs WHERE status = 'PENDING'")
    long pendingCount();

    @Query("SELECT COUNT(*) FROM pending_logs WHERE status IN ('PENDING', 'RETRY')")
    long deliverableCount();

    @Query("SELECT COUNT(*) FROM pending_logs")
    long totalCount();

    @Query("SELECT COALESCE(SUM(LENGTH(payload)), 0) FROM pending_logs")
    long payloadBytes();

    @Query("DELETE FROM pending_logs WHERE createdAt < :cutoff AND level IN ('DEBUG', 'INFO') "
            + "AND status != 'DEAD'")
    int deleteExpiredLowPriority(long cutoff);

    @Query("DELETE FROM pending_logs WHERE id IN (SELECT id FROM pending_logs "
            + "WHERE level IN ('DEBUG', 'INFO') AND status != 'DEAD' ORDER BY id ASC LIMIT :limit)")
    int deleteOldestLowPriority(int limit);

    @Query("DELETE FROM pending_logs WHERE id IN (SELECT id FROM pending_logs ORDER BY id ASC LIMIT :limit)")
    int deleteOldest(int limit);

    @Query("UPDATE pending_logs SET status = 'RETRY', attemptCount = :attemptCount, "
            + "nextAttemptAt = :nextAttemptAt, firstFailedAt = :firstFailedAt, lastError = :lastError "
            + "WHERE id = :id")
    void markRetry(long id, int attemptCount, long nextAttemptAt, long firstFailedAt, String lastError);

    @Query("UPDATE pending_logs SET status = 'DEAD', attemptCount = :attemptCount, "
            + "nextAttemptAt = 0, firstFailedAt = :firstFailedAt, lastError = :lastError WHERE id = :id")
    void markDead(long id, int attemptCount, long firstFailedAt, String lastError);

    @Query("DELETE FROM pending_logs WHERE id IN (:ids)")
    void deleteIds(List<Long> ids);
}
