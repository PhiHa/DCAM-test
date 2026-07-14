package com.dvid.dcam.platform.auth;

import com.dvid.dcam.platform.database.entities.UserAuthMethodEntity;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.bcrypt.BCrypt;

/** BDMA-compatible bcrypt password hashing. */
final class BcryptPasswordHasher {
    static final String ALGORITHM = "BCRYPT";
    static final int COST = 10;

    PasswordCredential hash(String passwordText) {
        if (passwordText == null || passwordText.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        byte[] hash = BCrypt.hashpw(passwordText, BCrypt.gensalt(COST))
                .getBytes(StandardCharsets.UTF_8);
        return new PasswordCredential(ALGORITHM, new byte[0], hash, COST);
    }

    boolean matches(String passwordText, UserAuthMethodEntity credential) {
        if (credential == null
                || credential.credentialAlgorithm == null
                || credential.credentialSalt == null
                || credential.credentialHash == null) {
            return false;
        }
        return matches(passwordText, new PasswordCredential(
                credential.credentialAlgorithm,
                credential.credentialSalt,
                credential.credentialHash,
                credential.credentialIterations));
    }

    boolean matches(String passwordText, PasswordCredential credential) {
        if (passwordText == null
                || credential == null
                || !ALGORITHM.equals(credential.algorithm())
                || credential.hash() == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(passwordText, new String(credential.hash(), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    boolean needsRehash(UserAuthMethodEntity credential) {
        return credential != null
                && ALGORITHM.equals(credential.credentialAlgorithm)
                && credential.credentialIterations != COST;
    }
}
