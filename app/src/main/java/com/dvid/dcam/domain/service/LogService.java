package com.dvid.dcam.domain.service;

/** Local-first diagnostics boundary. Cloud upload remains an optional adapter concern. */
public interface LogService {
    void info(String message);
    void warn(String message, Throwable error);
    void error(String message, Throwable error);
}
