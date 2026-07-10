package com.dvid.dcam.platform.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_auth_method",
        foreignKeys = @ForeignKey(
                entity = UserProfileEntity.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {
                @Index("user_id"),
                @Index(value = {"user_id", "method_type"}, unique = true)
        })
public final class UserAuthMethodEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "auth_method_id")
    public String authMethodId;
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;
    @NonNull
    @ColumnInfo(name = "method_type")
    public String methodType;
    @NonNull
    @ColumnInfo(name = "credential_algorithm")
    public String credentialAlgorithm;
    @NonNull
    @ColumnInfo(name = "credential_salt", typeAffinity = ColumnInfo.BLOB)
    public byte[] credentialSalt;
    @NonNull
    @ColumnInfo(name = "credential_hash", typeAffinity = ColumnInfo.BLOB)
    public byte[] credentialHash;
    @ColumnInfo(name = "credential_iterations")
    public int credentialIterations;
    @NonNull
    public String status;
    @ColumnInfo(name = "created_at")
    public long createdAt;
    @ColumnInfo(name = "updated_at")
    public long updatedAt;
    public long revision;

    public UserAuthMethodEntity(
            @NonNull String authMethodId,
            @NonNull String userId,
            @NonNull String methodType,
            @NonNull String credentialAlgorithm,
            @NonNull byte[] credentialSalt,
            @NonNull byte[] credentialHash,
            int credentialIterations,
            @NonNull String status,
            long createdAt,
            long updatedAt,
            long revision) {
        this.authMethodId = authMethodId;
        this.userId = userId;
        this.methodType = methodType;
        this.credentialAlgorithm = credentialAlgorithm;
        this.credentialSalt = credentialSalt;
        this.credentialHash = credentialHash;
        this.credentialIterations = credentialIterations;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.revision = revision;
    }
}
