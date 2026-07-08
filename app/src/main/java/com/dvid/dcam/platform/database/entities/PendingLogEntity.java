package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "pending_logs", indices = {@Index(value = "eventId", unique = true)})
public final class PendingLogEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public final String eventId;
    public final long createdAt;
    public final String level;
    public final String payload;
    @NonNull
    @ColumnInfo(defaultValue = "'PENDING'")
    public String status;
    @ColumnInfo(defaultValue = "0")
    public int attemptCount;
    @ColumnInfo(defaultValue = "0")
    public long nextAttemptAt;
    @ColumnInfo(defaultValue = "0")
    public long firstFailedAt;
    public String lastError;

    public PendingLogEntity(String eventId, long createdAt, String level, String payload) {
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.level = level;
        this.payload = payload;
        status = "PENDING";
    }
}
