package com.dvid.dcam.platform.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LogglyOutboxSenderTest {
    @Test
    void retryDelayIsIndependentAndExponentiallyCapped() {
        assertEquals(10_000L, LogglyOutboxSender.retryDelayMillis(1));
        assertEquals(20_000L, LogglyOutboxSender.retryDelayMillis(2));
        assertEquals(5_120_000L, LogglyOutboxSender.retryDelayMillis(10));
        assertEquals(18_000_000L, LogglyOutboxSender.retryDelayMillis(12));
        assertEquals(18_000_000L, LogglyOutboxSender.retryDelayMillis(20));
    }

    @Test
    void retryJobDoesNotReuseImmediateUploadJobId() {
        assertFalse(LogUploadScheduler.isRetryJob(0xDC04));
        assertTrue(LogUploadScheduler.isRetryJob(0xDC05));
    }
}
