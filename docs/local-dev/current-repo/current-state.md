# Current repository state

This page describes the refactored code visible on 2026-07-08. It records implementation reality and compares it with the approved Data Contract baseline now refreshed to 1.6; it is not a substitute for the contract or formal requirements.

## Build and platform

- One Android application module: `:app`, namespace/application ID `com.dvid.dcam`.
- Java-only build; Java 17 source/target and Java 21 Gradle toolchain.
- Android Gradle Plugin 9.2.1; min SDK 26, target SDK 36, compile SDK 36.1.
- CameraX 1.6.1, Room 2.8.4, WorkManager 2.11.2.
- AndroidX ViewModel/LiveData 2.11.0 and ViewBinding are enabled.
- JUnit Jupiter 6.1.0 is used for local tests.
- No Retrofit, dependency-injection framework, or Kotlin dependency is present.

The SDK values remain implementation choices. Data Contract 1.6 now fixes the logical database location, purpose, access rights, and need for schema/app/contract versioning, while the exact SQLite table details and final device/SDK policy remain TBD.

## Refactored architecture

```text
touch UI / hardware keys
    ↓ capture use cases
app/presentation: MainViewModel + LiveData<MainUiState>
    ↓ application-owned boundaries
feature/*/application: use cases, repositories, and capability boundaries
    ↓
platform/*: Android, CameraX, MediaRecorder, storage, device, provider implementations
```

| Package family | Responsibility |
|---|---|
| `app` | Android entry point, cross-feature navigation/presentation, and `AppComposition` wiring |
| `feature.*.domain` | Entities, immutable values, enums, and domain rules only |
| `feature.*.application.usecase` | Application workflows/interactors |
| `feature.*.application.port` | Explicit Java interfaces owned by the application layer |
| `feature.*.application.repository` | Pure application/core repository implementations when needed |
| `feature.*.presentation` | Feature-owned presentation state/ViewModel when needed |
| `platform.camera`, `platform.audio`, `platform.recording` | CameraX, MediaRecorder, and foreground-service adapters |
| `platform.storage`, `platform.database`, `platform.config` | Filesystem/media output, Room, and local CSON adapters |
| `platform.device`, `platform.input`, `platform.permission` | Android device APIs, physical-key routing, and runtime permissions |
| `platform.logging` | Local diagnostics plus Loggly/WorkManager/Room implementations |
| `core.config`, `core.logging` | Shared capabilities using the same domain/application vocabulary |

The source-level map and dependency rules live in `app/src/main/java/com/dvid/dcam/ARCHITECTURE.md`.

## Current user/application flow

- `MainActivity` is the Android entry point and ViewBinding presentation shell.
- Main operation keeps Android status and navigation bars visible so operators can still see battery, network/Wi-Fi, GPS/location, notifications, and navigation controls. Dedicated-screen/kiosk behavior remains a Technical Design/device-policy decision.
- `AppComposition` selects concrete adapters/repositories, loads configuration, and creates the lifecycle-bound capture runtime before attaching it to `MainViewModel`.
- Physical keys route through `HardwareButtonRouter` to the same capture use cases used by touch UI.
- Static global `Runnable` action registries were removed.
- Camera preview UI is separated from the CameraX capture gateway: `CameraXPreviewView` owns the visible surface/status text, while `CameraXCameraGatewayImpl` owns CameraX binding and recording commands.
- CameraX events are mapped to domain capture events before they update UI state.
- SOS handoff serializes stop/finalize/start rather than overlapping CameraX recordings.
- Active video/SOS recording starts an Android foreground service and persistent notification.
- Project-phase feature gates are persisted in Android SharedPreferences. Image, video, and audio capture are enabled by default; media browser, settings categories, GPS, security/encryption, cloud/network, transfer, and streaming are disabled by default.
- The menu is driven by `MainMenuModel`, keeps the declared order, hides disabled feature tiles, and falls back to Menu when a disabled screen is requested. About remains visible.
- Physical hardware capture keys now consult the same feature gates before invoking photo, video/SOS, or audio commands.
- A hidden Developer settings screen can toggle project-phase feature gates; it is reached from About through the local developer unlock gesture.
- The settings home presents a three-column launcher with 12 categories when the corresponding gates are enabled. Draft/demo setting controls are not counted as completed operational settings.
- Files is a read-only explorer limited to contract media roots `Video`, `IMP`, `Image`, and `Audio`. It supports folder navigation and opens media through a temporary FileProvider grant.
- Battery percentage, available app-storage bytes, and GPS capability/enabled state flow through `DeviceRepository`, a refresh use case, and ViewModel state.
- Language selection is implemented through a settings use case and Android SharedPreferences; changing language recreates the Activity with the localized context.

## Current storage behavior

`storage.mode` still accepts the prototype values `APP_DATA` (default) and `PUBLIC_DCIM`. The approved Internal/External/Auto behavior and Auto fallback are not implemented because physical root mapping and operational-settings persistence still require design.

| Type | Folder | Extension |
|---|---|---|
| Video | `Media/Video` | `.mp4` |
| SOS/important | `Media/IMP` | `.mp4` |
| Image | `Media/Image` | `.jpg` |
| Audio | `Media/Audio` | `.aac` using AAC/ADTS |

Current filename shape:

```text
DCAM_<CameraID>_<UserID>_<yyyyMMdd>_<HHmmss>[_IMP][_enc].<ext>
```

The listed media layout, naming, and AAC output align with the corresponding Data Contract 1.6 media artifact rules. SOS remains an application action/state name, but its persisted artifact is contract-important media in `IMP` with `_IMP`. Configuration is still the legacy `configs.cson` with existing `account.user_id`, `police.user_id`, encryption password, and operational keys; the approved device-information-only `Config/dcam_config.cson`, app/contract metadata, provisioning identity mirror, and DB-backed operational settings are not implemented.

AES-256-CTR media encryption now exists in `BodycamMediaCrypto` and is applied to saved photo, video/SOS, and audio artifacts when the Security/Encryption gate and saved media-encryption preference are enabled. Encrypted files use the existing `_enc` filename marker. Final key management, BDMA decryption compatibility evidence, and operational policy remain open and must not be treated as fully approved security design.

MP4 MD5 generation is not implemented. The contract fixes its scope and sidecar naming, but the sidecar content representation, enablement persistence, generation/finalization workflow, and BDMA fixture still need agreement. There is also no contract-aligned embedded media metadata implementation, media/database schema version, persisted file lifecycle, cross-root BDMA scanner/importer, or verified cleanup/write-back flow.

## Logging and database

- Local Logcat plus `Logs/logs.txt` under the existing app-storage root, with dated `logs-YYYY-MM-DD.txt` rotation and 14-day retention. The filename now matches the contract, but final internal-root mapping, ADB exposure, and BDMA read-only enforcement remain unverified.
- Context includes version, thread, source, hardware ID, model, and camera/account ID.
- Room-backed `dcam.db` currently stores only the pending Loggly outbox. Its filename matches the contract, but its schema does not yet cover contracted user, device-tracking, and operational data/configuration or BDMA-safe write-back.
- `LogSink` isolates capture/audio application-facing diagnostics from `DcamLogger`.
- Loggly remains a concrete provider inside the logging adapter package. Remote upload enablement is now controlled by the Cloud/Network feature gate; a full provider-neutral diagnostics design is still future work.

## Future capabilities

Location, Metadata, durable media lifecycle, MP4 checksum, contract configuration/database migration, Cloud, Remote Config, Update, Streaming, and PTT remain roadmap/documentation capabilities only. Contract media folder/naming rules and local AES-256-CTR media transforms have concrete platform implementation; broader capabilities enter source only when approved behavior defines their domain values and application boundaries.

## Local property behavior

The Gradle build exports keys loaded from `application.properties` and gitignored `application-local.properties` into `BuildConfig`.

Private workflow instructions for local-property handling belong in `application-local.properties`, not public build files.

## Remaining gaps

- Each approved menu feature still needs its own XML/ViewBinding screen and feature ViewModel/use cases beyond the feature-gated launcher/shell.
- `DemoSettingsState` and draft/demo setting controls are local UI scaffolding, not accepted implementation evidence.
- CameraX is lifecycle-owned by the Activity adapter; the foreground service does not yet own/recover recording after process death.
- HandlerThread/vendor-SDK serialization is scaffolded by architecture, but CameraX currently uses its lifecycle/main-executor contract.
- No GPS/location implementation or metadata integration yet.
- Device status currently covers battery, available app storage, and GPS capability/enabled state; richer network/firmware/USB status remains future work.
- No persistent media status/recovery state machine.
- Data Contract media folders/naming, important-media mapping, AAC output, active log filename, and local AES-256-CTR transforms are implemented locally. Storage modes/physical roots, device-information-only CSON, app/contract metadata, DB-backed settings, MP4 MD5, embedded metadata, final key handling, BDMA permissions, import results, cleanup, and E2E proof remain open.
- Android Device Operation: existing permission/foreground-notification behavior is present, and system bars are intentionally visible. Boot receiver, Home/Launcher role, managed kiosk/exit control, exact dedicated-screen behavior, screen/power policy, durable service ownership, and crash/reboot recovery remain pending Technical Design/device policy.
- No streaming, PTT, cloud, remote config, update, or Device/User implementation beyond boundaries/placeholders.
- Real BodyCamera POC and hardware matrix validation remain mandatory.

## Verification

After refactoring:

- `testDebugUnitTest`: the prior 2026-07-07 baseline passed 22 tests, including architecture, immutable UI-state, capture rebinding, contract media paths/naming, legacy config, and media-browser sandbox guards. The 2026-07-08 local doc refresh counted 34 test methods after adding feature-gate and media-crypto coverage; Gradle was not re-run for this documentation-only update.
- `assembleDebug`: the prior 2026-07-07 baseline succeeded.
- `lintDebug`: last verified as succeeding on 2026-07-06; the final 2026-07-07 re-run was blocked when the sandboxed Gradle wrapper attempted a network download.
- Generated `BuildConfig`: intentionally contains all application and local-property fields.
