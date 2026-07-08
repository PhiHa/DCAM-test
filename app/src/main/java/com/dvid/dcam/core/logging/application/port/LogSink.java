package com.dvid.dcam.core.logging.application.port;

/** Local-first diagnostics capability. Cloud upload remains an adapter concern. */
public interface LogSink {
    void info(String message);
    void warn(String message, Throwable error);
    void error(String message, Throwable error);
}
