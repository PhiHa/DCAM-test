package com.dvid.dcam.platform.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogglyUploadWorkerTest {
    @Test
    void retryDelayIsIndependentAndExponentiallyCapped() {
        assertEquals(10_000L, LogglyUploadWorker.retryDelayMillis(1));
        assertEquals(20_000L, LogglyUploadWorker.retryDelayMillis(2));
        assertEquals(5_120_000L, LogglyUploadWorker.retryDelayMillis(10));
        assertEquals(18_000_000L, LogglyUploadWorker.retryDelayMillis(12));
        assertEquals(18_000_000L, LogglyUploadWorker.retryDelayMillis(20));
    }
}
