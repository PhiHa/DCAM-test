package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_profile",
        indices = {
                @Index(value = "file_user_id", unique = true)
        })
public final class UserProfileEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;
    @NonNull
    @ColumnInfo(name = "file_user_id")
    public String fileUserId;
    @NonNull
    @ColumnInfo(name = "display_name")
    public String displayName;
    @NonNull
    public String status;
    @NonNull
    public String source;
    @ColumnInfo(name = "created_at")
    public long createdAt;
    @ColumnInfo(name = "updated_at")
    public long updatedAt;
    public long revision;

    public UserProfileEntity(
            @NonNull String userId,
            @NonNull String fileUserId,
            @NonNull String displayName,
            @NonNull String status,
            @NonNull String source,
            long createdAt,
            long updatedAt,
            long revision) {
        this.userId = userId;
        this.fileUserId = fileUserId;
        this.displayName = displayName;
        this.status = status;
        this.source = source;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.revision = revision;
    }
}
