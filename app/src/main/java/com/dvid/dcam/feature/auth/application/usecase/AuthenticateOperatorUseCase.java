package com.dvid.dcam.feature.auth.application.usecase;

import com.dvid.dcam.feature.auth.domain.LoginCredentials;
import com.dvid.dcam.feature.auth.domain.LoginResult;

public interface AuthenticateOperatorUseCase {
    LoginResult execute(LoginCredentials credentials);
}
