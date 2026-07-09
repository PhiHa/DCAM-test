package com.dvid.dcam.feature.auth.domain;

/** Credentials deliberately support both MVP password-only and later username/password login. */
public final class LoginCredentials {
    private final String identifier;
    private final String passwordText;

    private LoginCredentials(String identifier, String passwordText) {
        this.identifier = identifier;
        this.passwordText = passwordText == null ? "" : passwordText;
    }

    public static LoginCredentials passwordOnly(String passwordText) {
        return new LoginCredentials(null, passwordText);
    }

    public static LoginCredentials usernameAndPassword(String identifier, String passwordText) {
        return new LoginCredentials(identifier, passwordText);
    }

    public String getIdentifier() { return identifier; }
    public String getPasswordText() { return passwordText; }
}
