package com.dvid.dcam.platform.auth;

import com.dvid.dcam.platform.database.entities.UserAuthMethodEntity;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2-HMAC-SHA256 password hashing with a unique random salt per credential. */
final class Pbkdf2PasswordHasher {
    static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    static final int ITERATIONS = 600_000;
    private static final int MIN_ACCEPTED_ITERATIONS = 100_000;
    private static final int MAX_ACCEPTED_ITERATIONS = 1_000_000;
    private static final int HASH_BITS = 256;
    private static final int SALT_BYTES = 16;

    private final SecureRandom secureRandom;

    Pbkdf2PasswordHasher() {
        this(new SecureRandom());
    }

    Pbkdf2PasswordHasher(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    PasswordCredential hash(String passwordText) {
        if (passwordText == null || passwordText.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        byte[] hash = derive(passwordText, salt, ITERATIONS);
        return new PasswordCredential(ALGORITHM, salt, hash, ITERATIONS);
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
                || credential.iterations() < MIN_ACCEPTED_ITERATIONS
                || credential.iterations() > MAX_ACCEPTED_ITERATIONS) {
            return false;
        }
        byte[] actual = derive(passwordText, credential.salt(), credential.iterations());
        try {
            return MessageDigest.isEqual(credential.hash(), actual);
        } finally {
            Arrays.fill(actual, (byte) 0);
        }
    }

    private static byte[] derive(String passwordText, byte[] salt, int iterations) {
        char[] password = passwordText.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException error) {
            throw new IllegalStateException("PBKDF2 password hashing unavailable", error);
        } finally {
            spec.clearPassword();
            Arrays.fill(password, '\0');
        }
    }
}
