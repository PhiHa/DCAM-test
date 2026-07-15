# DCAM Coding Conventions

## Logging

Operational events must use `com.dvid.dcam.platform.logging.DcamLogger`:

- `DcamLogger.i(...)` for normal lifecycle and state events.
- `DcamLogger.w(...)` for recoverable failures, with the `Throwable` when available.
- `DcamLogger.e(...)` for failed operations and crash events, with the `Throwable` when available.

Production code must not call `android.util.Log`, `System.out`, `System.err`, or
`printStackTrace()` directly. Direct Android log calls inside `DcamLogger` are implementation
details for Logcat. Operational events must still pass through local `logs.txt`, Room outbox,
and Loggly delivery logic.

Feature and application code that cannot depend on Android platform code must use `LogSink`.

Logs must remain useful offline, exclude passwords, tokens, secrets, and sensitive payloads,
and never block capture or storage when remote delivery is unavailable.
