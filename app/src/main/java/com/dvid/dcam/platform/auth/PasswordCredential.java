package com.dvid.dcam.platform.auth;

import java.util.Arrays;

/** Immutable persisted representation of a salted, one-way password hash. */
final class PasswordCredential {
    private final String algorithm;
    private final byte[] salt;
    private final byte[] hash;
    private final int iterations;

    PasswordCredential(String algorithm, byte[] salt, byte[] hash, int iterations) {
        this.algorithm = algorithm;
        this.salt = Arrays.copyOf(salt, salt.length);
        this.hash = Arrays.copyOf(hash, hash.length);
        this.iterations = iterations;
    }

    String algorithm() {
        return algorithm;
    }

    byte[] salt() {
        return Arrays.copyOf(salt, salt.length);
    }

    byte[] hash() {
        return Arrays.copyOf(hash, hash.length);
    }

    int iterations() {
        return iterations;
    }
}
