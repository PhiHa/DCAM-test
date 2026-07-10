package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "operator_session",
        foreignKeys = @ForeignKey(
                entity = UserProfileEntity.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.RESTRICT),
        indices = {
                @Index("user_id"),
                @Index(value = "active_slot", unique = true),
                @Index(value = {"status", "boot_id"})
        })
public final class OperatorSessionEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "session_id")
    public String sessionId;
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;
    @NonNull
    @ColumnInfo(name = "file_user_id_snapshot")
    public String fileUserIdSnapshot;
    @NonNull
    @ColumnInfo(name = "display_name_snapshot")
    public String displayNameSnapshot;
    @NonNull
    @ColumnInfo(name = "boot_id")
    public String bootId;
    @NonNull
    public String status;
    @ColumnInfo(name = "active_slot")
    public Integer activeSlot;
    @ColumnInfo(name = "started_at")
    public long startedAt;
    @ColumnInfo(name = "ended_at")
    public Long endedAt;
    @ColumnInfo(name = "end_reason")
    public String endReason;
    public long revision;

    public OperatorSessionEntity(
            @NonNull String sessionId,
            @NonNull String userId,
            @NonNull String fileUserIdSnapshot,
            @NonNull String displayNameSnapshot,
            @NonNull String bootId,
            @NonNull String status,
            Integer activeSlot,
            long startedAt,
            Long endedAt,
            String endReason,
            long revision) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.fileUserIdSnapshot = fileUserIdSnapshot;
        this.displayNameSnapshot = displayNameSnapshot;
        this.bootId = bootId;
        this.status = status;
        this.activeSlot = activeSlot;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.endReason = endReason;
        this.revision = revision;
    }
}
