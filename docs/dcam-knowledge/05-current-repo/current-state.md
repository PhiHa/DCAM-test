# Current repository state

This page describes the refactored code visible on 2026-07-03. It records implementation reality, not an approved Data Contract or substitute for formal requirements.

## Build and platform

- One Android application module: `:app`, namespace/application ID `com.dvid.dcam`.
- Java-only build; Java 17 source/target and Java 21 Gradle toolchain.
- Android Gradle Plugin 9.2.1; min SDK 26, target SDK 36, compile SDK 36.1.
- CameraX 1.6.1, Room 2.8.4, WorkManager 2.11.2.
- AndroidX ViewModel/LiveData 2.11.0 and ViewBinding are enabled.
- JUnit Jupiter 6.1.0 is used for local tests.
- No Retrofit, dependency-injection framework, or Kotlin dependency is present.

The SDK/database values are implementation choices; Confluence still marks the final device matrix, SDK policy, and long-term database/Data Contract strategy TBD.

## Refactored architecture

```text
touch UI / hardware keys
    ↓ DcamCommandHandler
MainViewModel + LiveData<MainUiState>
    ↓ individual use cases
CaptureRepository / ConfigurationRepository
    ↓ domain service interfaces
CameraX, MediaRecorder, local config/storage, device, logging adapters
```

| Package | Responsibility |
|---|---|
| `presentation` | Immutable UI state, screen state, ViewModel, factory |
| `application.command` | Commands shared by UI and physical buttons |
| `application.usecase` | Capture workflow entry points |
| `domain.model` | Android-independent config/device/capture models |
| `domain.repository` | Capture and configuration boundaries |
| `domain.service` | Camera/audio/device/log plus explicit future capability boundaries |
| `domain.event` | Platform-to-domain capture result callbacks |
| `data.repository` | Default capture coordination and local config resolution |
| `camera`, `audio`, `device`, `storage`, `logging` | Current platform/provider adapters |
| `platform.recording` | Foreground recording service/notification |
| `data.db` | Room manifest, DAO/entity, migrations, schema exports |
| `input` | Instance-based physical-key routing; no global mutable actions |

The source-level map and dependency rules live in `app/src/main/java/com/dvid/dcam/ARCHITECTURE.md`.

## Current user/application flow

- `MainActivity` is the Android composition root and presentation shell.
- It inflates dedicated camera/menu/placeholder ViewBinding layouts, creates adapters/repositories, attaches them to `MainViewModel`, and renders LiveData state.
- Physical keys route through `HardwareButtonRouter` to the same application commands used by touch UI.
- Static global `Runnable` action registries were removed.
- CameraX events are mapped to domain capture events before they update UI state.
- SOS handoff serializes stop/finalize/start rather than overlapping CameraX recordings.
- Active video/SOS recording starts an Android foreground service and persistent notification.
- Menu feature bodies remain placeholders, but their shared shells now use XML/ViewBinding rather than programmatic view construction.
- The settings home presents a three-column launcher with 12 clear categories. Opening a settings item shows a short non-editable OEM/CSON setting-name list; sensitive values are not shown.
- Files is a read-only explorer limited to DCAM `video`, `SOS`, `image`, and `audio` roots. It supports folder navigation and opens media through a temporary FileProvider grant.
- Battery percentage, available app-storage bytes, and GPS capability/enabled state flow through DeviceService, DeviceRepository, a refresh use case, and ViewModel state.

## Current storage behavior

`storage.mode` selects app data (default) or public DCIM. Current prototype folders are:

| Type | Folder | Extension |
|---|---|---|
| Video | `video/<yyyy-MM-dd>/` | `.mp4` |
| SOS | `SOS/<yyyy-MM-dd>/` | `.mp4` |
| Image | `image/<yyyy-MM-dd>/` | `.jpg` |
| Audio | `audio/<yyyy-MM-dd>/` | `.m4a` |

Current filename shape:

```text
DSJ_<accountUserId>_<policeUserId>_<yyyyMMdd>_<HHmmss>[_SOS][_enc].<ext>
```

The current local config is `configs.cson`. These folders, names, flags, and config fields remain prototype choices until Storage Design and the DCAM-BDMA Data Contract approve them.

There is still no general media metadata schema, schema version, file lifecycle persistence, checksum/integrity record, or BDMA discovery/import implementation.

## Logging and database

- Local Logcat plus rotating `app.log`, with 14-day local retention.
- Context includes version, thread, source, hardware ID, model, and camera/account ID.
- Room-backed pending Loggly outbox with WorkManager upload/retry.
- `LogService` now isolates capture/audio application-facing diagnostics from `DcamLogger`.
- Loggly remains a concrete provider inside the logging adapter package; a full provider-neutral diagnostics design is still future work.

## Future capability scaffolds

The service catalog now reserves boundaries for Location, Metadata, Storage Contract, File Integrity, Security, Cloud, Remote Config, Update, Streaming, and PTT.

These interfaces are intentionally method-free. Their Confluence contracts are TBD, so adding speculative methods would silently invent metadata, encryption, cloud, update, streaming, or PTT decisions.

## Local property behavior

The Gradle build exports keys loaded from `application.properties` and gitignored `application-local.properties` into `BuildConfig`.

Private workflow instructions for local-property handling belong in `application-local.properties`, not public build files.

## Remaining gaps

- Each approved menu feature still needs its own XML/ViewBinding screen and feature ViewModel/use cases.
- CameraX is lifecycle-owned by the Activity adapter; the foreground service does not yet own/recover recording after process death.
- HandlerThread/vendor-SDK serialization is scaffolded by architecture, but CameraX currently uses its lifecycle/main-executor contract.
- No GPS/location implementation or metadata integration yet.
- Device status currently covers battery, available app storage, and GPS capability/enabled state; richer network/firmware/USB status remains future work.
- No persistent media status/recovery state machine.
- No approved metadata, storage, checksum, encryption, or BDMA contract.
- No streaming, PTT, cloud, remote config, update, or Device/User implementation beyond boundaries/placeholders.
- Real BodyCamera POC and hardware matrix validation remain mandatory.

## Verification

After refactoring:

- `testDebugUnitTest`: 17 tests pass, including architecture, immutable UI-state, and media-browser sandbox guards.
- `assembleDebug`: succeeds.
- `lintDebug`: succeeds.
- Generated `BuildConfig`: intentionally contains all application and local-property fields.
