package com.dvid.dcam.feature.auth.domain;

/** Result object keeps expected login failures out of exception-based UI control flow. */
public final class LoginResult {
    private final OperatorSession session;
    private final LoginFailure failure;

    private LoginResult(OperatorSession session, LoginFailure failure) {
        this.session = session;
        this.failure = failure;
    }

    public static LoginResult success(OperatorSession session) {
        return new LoginResult(session, null);
    }

    public static LoginResult failure(LoginFailure failure) {
        return new LoginResult(null, failure);
    }

    public boolean isSuccess() { return session != null; }
    public OperatorSession getSession() { return session; }
    public LoginFailure getFailure() { return failure; }
}
