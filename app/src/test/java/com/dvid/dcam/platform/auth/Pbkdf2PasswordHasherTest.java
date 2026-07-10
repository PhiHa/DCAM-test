package com.dvid.dcam.platform.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

final class Pbkdf2PasswordHasherTest {
    private final Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher();

    @Test void verifiesCorrectPasswordAndRejectsWrongPassword() {
        PasswordCredential credential = hasher.hash("493027");

        assertTrue(hasher.matches("493027", credential));
        assertFalse(hasher.matches("493028", credential));
    }

    @Test void usesUniqueSaltForEachCredential() {
        PasswordCredential first = hasher.hash("493027");
        PasswordCredential second = hasher.hash("493027");

        assertFalse(Arrays.equals(first.salt(), second.salt()));
        assertFalse(Arrays.equals(first.hash(), second.hash()));
    }
}
