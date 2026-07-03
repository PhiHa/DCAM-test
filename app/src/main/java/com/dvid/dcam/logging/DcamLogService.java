package com.dvid.dcam.logging;

import com.dvid.dcam.domain.service.LogService;

/** Adapter from application-facing diagnostics to the current local/Loggly implementation. */
public final class DcamLogService implements LogService {
    @Override public void info(String message) { DcamLogger.i(message); }
    @Override public void warn(String message, Throwable error) { DcamLogger.w(message, error); }
    @Override public void error(String message, Throwable error) { DcamLogger.e(message, error); }
}
