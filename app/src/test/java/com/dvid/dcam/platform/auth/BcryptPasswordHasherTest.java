package com.dvid.dcam.platform.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

final class BcryptPasswordHasherTest {
    private final BcryptPasswordHasher hasher = new BcryptPasswordHasher();

    @Test void verifiesCorrectPasswordAndRejectsWrongPassword() {
        PasswordCredential credential = hasher.hash("493027");

        assertTrue(hasher.matches("493027", credential));
        assertFalse(hasher.matches("493028", credential));
    }

    @Test void usesUniqueEmbeddedSaltForEachCredential() {
        PasswordCredential first = hasher.hash("493027");
        PasswordCredential second = hasher.hash("493027");

        assertFalse(Arrays.equals(first.hash(), second.hash()));
    }

    @Test void usesBdmaCost() {
        assertEquals(10, hasher.hash("493027").iterations());
    }
}
