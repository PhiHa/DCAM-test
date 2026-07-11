package com.dvid.dcam.platform.logging;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DcamLoggerTest {
    @Test
    void logglyMessageContainsOnlyCallerSuppliedMessage() {
        String payload = DcamLogger.json(
                "Recording started\nCamera \"front\"",
                null,
                "capture-thread",
                "CaptureController");

        assertTrue(payload.contains("\"message\":\"Recording started\\nCamera \\\"front\\\"\""));
        assertTrue(payload.contains("\"timestamp\":\""));
        assertFalse(payload.contains("\"message\":\"20"));
        assertFalse(payload.contains(" thread=\\\"capture-thread\\\""));
        assertFalse(payload.contains(" source=CaptureController"));
    }
}
