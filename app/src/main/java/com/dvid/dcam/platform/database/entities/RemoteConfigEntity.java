package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "remote_config_cache")
public final class RemoteConfigEntity {
    @PrimaryKey
    public int id;
    public String revision;
    public String payloadJson;
    @NonNull
    @ColumnInfo(defaultValue = "'NONE'")
    public String status;
    public String lastError;
    @ColumnInfo(defaultValue = "0")
    public long updatedAt;
    @ColumnInfo(defaultValue = "0")
    public long appliedAt;

    public RemoteConfigEntity(
            int id,
            String revision,
            String payloadJson,
            @NonNull String status,
            String lastError,
            long updatedAt,
            long appliedAt) {
        this.id = id;
        this.revision = revision;
        this.payloadJson = payloadJson;
        this.status = status;
        this.lastError = lastError;
        this.updatedAt = updatedAt;
        this.appliedAt = appliedAt;
    }
}
