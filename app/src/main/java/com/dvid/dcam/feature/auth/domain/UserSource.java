package com.dvid.dcam.feature.auth.domain;

/** Origin of an operator account. The value is persisted for audit and sync policy. */
public enum UserSource {
    DEFAULT,
    DEVELOPER,
    CLOUD
}
