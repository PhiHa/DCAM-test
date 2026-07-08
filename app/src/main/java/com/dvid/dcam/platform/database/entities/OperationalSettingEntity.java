package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "operational_settings")
public final class OperationalSettingEntity {
    @PrimaryKey
    @NonNull
    public String key;
    public String value;
    @ColumnInfo(defaultValue = "0")
    public long updatedAt;

    public OperationalSettingEntity(@NonNull String key, String value, long updatedAt) {
        this.key = key;
        this.value = value;
        this.updatedAt = updatedAt;
    }
}
