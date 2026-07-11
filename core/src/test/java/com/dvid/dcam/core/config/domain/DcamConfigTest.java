package com.dvid.dcam.core.config.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class DcamConfigTest {
    @Test void defaultsMediaEncryptionOff() {
        assertFalse(new DcamConfig().isVideoEncrypted());
        assertFalse(DcamConfig.defaults("camera-id").isVideoEncrypted());
    }

    @Test void explicitMediaEncryptionSettingIsPreserved() {
        assertTrue(new DcamConfig("camera-id", "operator-id", true).isVideoEncrypted());
    }
}
