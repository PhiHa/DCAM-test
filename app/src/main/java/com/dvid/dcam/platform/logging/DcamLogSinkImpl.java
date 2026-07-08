package com.dvid.dcam.platform.logging;

import com.dvid.dcam.core.logging.application.port.LogSink;

/** DCAM local/outbox implementation of the application logging sink. */
public final class DcamLogSinkImpl implements LogSink {
    @Override public void info(String message) { DcamLogger.i(message); }
    @Override public void warn(String message, Throwable error) { DcamLogger.w(message, error); }
    @Override public void error(String message, Throwable error) { DcamLogger.e(message, error); }
}
