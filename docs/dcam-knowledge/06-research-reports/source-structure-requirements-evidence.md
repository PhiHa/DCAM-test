# Source structure and requirement evidence report

Audit date: **2026-07-06** (Asia/Saigon)
Repository baseline: refactored working tree based on commit `9857cea` (`add documents`)
Documentation baseline: local Confluence digest snapshot dated **2026-07-03**

## Executive conclusion

The current project **substantially satisfies the documented architecture direction**, but it **only partially satisfies the MVP functional baseline** and does **not yet prove MVP acceptance or production readiness**.

The strongest evidence is a standardized feature-first Clean Architecture/Ports-and-Adapters structure with explicit `domain`, `application/usecase`, `application/port`, `presentation`, and outer `platform` responsibilities. A source-level architecture test enforces canonical package paths, boundary-interface placement/naming, dependency direction, implementation `Impl` naming, and the four approved top-level package families. Capture is local-first, physical and touch controls share the same use-case interfaces, configuration falls back to safe local defaults, and online log upload is asynchronous and optional.

The largest gaps are product-critical: no approved DCAM-BDMA Data Contract, per-media metadata/schema version, persisted media lifecycle, integrity/checksum, low-storage policy, GPS capture, process-death recovery, BDMA import proof, or real BodyCamera compatibility matrix. Speculative empty interfaces were removed; future capabilities remain documentation/roadmap items until approved use cases define real application boundaries.

### Verdict at a glance

| Question | Finding |
|---|---|
| Is the code modular? | **Yes at package/layer level; partially at build level.** There is one Gradle `:app` module, so isolation is source/test-enforced rather than compiler-enforced between Gradle modules. |
| Is it expandable? | **Yes structurally.** Feature slices, use cases, explicit application boundaries, platform implementations, and composition give clear extension seams without speculative source scaffolds. |
| Is core logic tied to one hardware implementation? | **Mostly no, but portability is not yet proven.** Feature application/domain code is Android- and vendor-independent; `AppComposition` selects the current CameraX adapter. Real devices and vendor fallbacks remain untested. |
| Is core operation tied to online services? | **No for current capture/storage.** The capture flow contains no cloud dependency. Loggly upload is token-gated, network-constrained, queued, and asynchronous. Diagnostics are nevertheless still concretely Loggly-oriented. |
| Does the project satisfy the full requirement baseline? | **No.** Architecture alignment is strong; functional MVP coverage is partial; acceptance targets need missing implementation plus device/BDMA/QA evidence. |

## Scope and evidence rules

This audit compares the source to:

- [MVP requirement baseline](../01-requirements/mvp-baseline.md)
- [System and application architecture](../02-architecture/system-architecture.md)
- [Data, storage, and DCAM-BDMA boundary](../02-architecture/data-and-bdma.md)
- [Cloud, quality, and security direction](../02-architecture/cloud-quality-security.md)
- [Android development standard](../03-development/android-standard.md)
- [Current repository state](../05-current-repo/current-state.md)

The formal Confluence Requirements area and DCAM-BDMA Data Contract were empty at the documentation snapshot. The IDs in this report are local traceability labels, not approved requirement IDs.

Status meanings:

| Status | Meaning |
|---|---|
| **Verified** | Direct source evidence and, where applicable, an automated check support the claim. |
| **Partial** | A meaningful implementation exists, but a required part or acceptance proof is missing. |
| **Scaffolded** | A boundary or placeholder exists without usable feature behavior. |
| **Gap** | No implementation evidence was found for the required behavior. |
| **External proof needed** | Source inspection cannot prove the claim; device, BDMA, performance, security, or QA evidence is required. |

## Actual source structure

The repository contains one Android application module, 67 production Java files, 11 local unit-test files, and 19 `@Test` methods at the audited baseline.

```text
:app
└── com.dvid.dcam
    ├── app/                          Entry point, composition, navigation, shared presentation
    ├── feature/
    │   ├── capture/                  domain/application vertical slice
    │   ├── media/                    domain/application vertical slice
    │   └── device/                   domain/application vertical slice
    ├── platform/                     Android/hardware/filesystem/provider implementations
    └── core/                         Shared config/logging using the same layer vocabulary
```

| Boundary | Direct evidence | Assessment |
|---|---|---|
| Build/module | [`settings.gradle`](../../../settings.gradle) includes only `:app`; [`app/build.gradle`](../../../app/build.gradle) enables Java, ViewBinding, CameraX, Room, WorkManager, LiveData, and ViewModel. | Package-modular monolith, not multi-module. Appropriate for the current size, but Gradle cannot enforce every internal boundary. |
| App shell | [`MainActivity`](../../../app/src/main/java/com/dvid/dcam/app/MainActivity.java) renders ViewBinding screens; [`MainViewModel`](../../../app/src/main/java/com/dvid/dcam/app/presentation/MainViewModel.java) exposes immutable cross-feature UI state. | Matches MVVM direction while keeping app lifecycle/navigation outside feature logic. |
| Domain/application | [`VideoRecordingUseCase`](../../../app/src/main/java/com/dvid/dcam/feature/capture/application/usecase/VideoRecordingUseCase.java), [`PhotoCaptureUseCase`](../../../app/src/main/java/com/dvid/dcam/feature/capture/application/usecase/PhotoCaptureUseCase.java), and [`AudioRecordingUseCase`](../../../app/src/main/java/com/dvid/dcam/feature/capture/application/usecase/AudioRecordingUseCase.java) depend on application boundaries such as [`CameraGateway`](../../../app/src/main/java/com/dvid/dcam/feature/capture/application/port/CameraGateway.java) and [`AudioRecorder`](../../../app/src/main/java/com/dvid/dcam/feature/capture/application/port/AudioRecorder.java); capture values remain under `domain`. | Use case, boundary, and domain ownership are explicit and platform-independent. Current use cases remain thin delegation rather than rich policy. |
| Application repositories | [`ConfigurationRepositoryImpl`](../../../app/src/main/java/com/dvid/dcam/core/config/application/repository/ConfigurationRepositoryImpl.java) implements a core repository boundary using [`ConfigurationSource`](../../../app/src/main/java/com/dvid/dcam/core/config/application/port/ConfigurationSource.java) and [`LogSink`](../../../app/src/main/java/com/dvid/dcam/core/logging/application/port/LogSink.java). | Pure application/core repository behavior stays framework-independent. |
| Platform implementations | [`CameraXCameraGatewayImpl`](../../../app/src/main/java/com/dvid/dcam/platform/camera/CameraXCameraGatewayImpl.java), [`AndroidAudioRecorderImpl`](../../../app/src/main/java/com/dvid/dcam/platform/audio/AndroidAudioRecorderImpl.java), [`AndroidDeviceRepositoryImpl`](../../../app/src/main/java/com/dvid/dcam/platform/device/AndroidDeviceRepositoryImpl.java), [`LocalMediaRepositoryImpl`](../../../app/src/main/java/com/dvid/dcam/platform/storage/LocalMediaRepositoryImpl.java), and [`DcamLogSinkImpl`](../../../app/src/main/java/com/dvid/dcam/platform/logging/DcamLogSinkImpl.java) implement application/core boundaries. | Android/provider code is visibly concentrated under `platform`. |
| Composition | [`AppComposition`](../../../app/src/main/java/com/dvid/dcam/app/AppComposition.java) selects adapters and repositories and creates the lifecycle-bound capture runtime. | Concrete selection is explicit and no longer mixed into Activity rendering. Alternate adapter selection and composition tests remain future work. |
| Boundary enforcement | [`LayerDependencyTest`](../../../app/src/test/java/com/dvid/dcam/architecture/LayerDependencyTest.java) checks dependency direction, canonical layer paths, public-port placement/naming, and approved top-level package families. | The intended architecture is executable rather than dependent on reviewer interpretation. |

One transitional leak is correctly confined to platform code: [`DcamMediaOutput`](../../../app/src/main/java/com/dvid/dcam/platform/storage/DcamMediaOutput.java) exposes CameraX output types to camera/storage adapters. Those types do not enter domain, application, feature adapters, or app-presentation contracts.

## Verified runtime flows

### Video, SOS, and image capture

```text
touch control ─┐
               ├─> MainViewModel
physical key ──┘       -> capture use case
                       -> CameraGateway
                       -> CameraXCameraGatewayImpl
                       -> DcamMediaOutput -> local file or MediaStore
                       -> CaptureEventUseCase
                       -> immutable MainUiState -> rendered UI
```

Evidence:

1. [`HardwareButtonRouter`](../../../app/src/main/java/com/dvid/dcam/platform/input/HardwareButtonRouter.java) maps BodyCamera keys to the same capture use cases used by touch UI; the router has focused unit tests.
2. [`MainViewModel`](../../../app/src/main/java/com/dvid/dcam/app/presentation/MainViewModel.java) delegates photo/video/SOS/audio actions to feature use cases and maps domain events to UI state.
3. [`VideoRecordingUseCaseImpl`](../../../app/src/main/java/com/dvid/dcam/feature/capture/application/usecase/VideoRecordingUseCaseImpl.java) coordinates video/SOS workflow against `CameraGateway` and capture event state.
4. [`CameraXCameraGatewayImpl`](../../../app/src/main/java/com/dvid/dcam/platform/camera/CameraXCameraGatewayImpl.java) maps CameraX start/finalize/photo/error callbacks to `CaptureEventUseCase`, serializes the SOS handoff by finalizing the active recording first, and starts/stops [`RecordingForegroundService`](../../../app/src/main/java/com/dvid/dcam/platform/recording/RecordingForegroundService.java).
5. [`DcamMediaOutputImpl`](../../../app/src/main/java/com/dvid/dcam/platform/storage/DcamMediaOutputImpl.java) translates logical media descriptors into app files or Android MediaStore operations.

This proves an implemented application flow. It does not prove the documented 99% success target, interruption recovery, or operation on every target BodyCamera.

### Local configuration and graceful fallback

```text
application/default property
    -> DcamStorage selection
    -> CsonConfigurationSourceImpl
    -> ConfigurationRepositoryImpl
    -> parsed DcamConfig OR safe defaults on error
```

[`CsonConfigurationSourceImpl`](../../../app/src/main/java/com/dvid/dcam/platform/config/CsonConfigurationSourceImpl.java) hides the local file store behind the core [`ConfigurationSource`](../../../app/src/main/java/com/dvid/dcam/core/config/application/port/ConfigurationSource.java) boundary. [`ConfigurationRepositoryImpl`](../../../app/src/main/java/com/dvid/dcam/core/config/application/repository/ConfigurationRepositoryImpl.java) logs parsing/loading failures through [`LogSink`](../../../app/src/main/java/com/dvid/dcam/core/logging/application/port/LogSink.java) and returns `DcamConfig.defaults(...)`, so optional configuration failure does not prevent startup. Remote/runtime precedence is not implemented.

### Device status

```text
AndroidDeviceRepositoryImpl -> DeviceRepository
    -> RefreshDeviceStatusUseCase -> MainViewModel -> MainUiState
```

[`AndroidDeviceRepositoryImpl`](../../../app/src/main/java/com/dvid/dcam/platform/device/AndroidDeviceRepositoryImpl.java) currently reads battery percentage, available app-storage bytes, and GPS capability/enabled status. It handles missing services/runtime failures with explicit unknown/unavailable states. It does not acquire GPS coordinates or cover network, GMS, USB, firmware, camera, or microphone capability in the status model.

### Local-first and optional online diagnostics

```text
capture/audio implementation -> LogSink -> DcamLogSinkImpl -> DcamLogger
                                      ├─> Logcat + rotating local app.log
                                      └─> Room outbox
                                            -> WorkManager (network required)
                                            -> Loggly only when token is configured
```

[`DcamLogger`](../../../app/src/main/java/com/dvid/dcam/platform/logging/DcamLogger.java) writes locally before queuing remote diagnostics. [`LogOutbox`](../../../app/src/main/java/com/dvid/dcam/platform/logging/LogOutbox.java) persists events, and [`LogUploadScheduler`](../../../app/src/main/java/com/dvid/dcam/platform/logging/LogUploadScheduler.java) requires a connected network. [`LogglyUploadWorker`](../../../app/src/main/java/com/dvid/dcam/platform/logging/LogglyUploadWorker.java) exits successfully when no token is configured. Therefore Internet/Loggly is not on the capture success path. However, the diagnostics implementation and naming are provider-specific, so general provider neutrality is incomplete.

### BDMA boundary

DCAM currently produces deterministic local media paths and a read-only in-app browser. [`DcamStorage`](../../../app/src/main/java/com/dvid/dcam/platform/storage/DcamStorage.java) creates type/date folders, [`DcamFileName`](../../../app/src/main/java/com/dvid/dcam/platform/storage/DcamFileName.java) creates deterministic prototype names, and [`LocalMediaRepositoryImpl`](../../../app/src/main/java/com/dvid/dcam/platform/storage/LocalMediaRepositoryImpl.java) restricts browsing to the four DCAM media roots and blocks path traversal.

There is no BDMA-side code in this repository, no approved discovery contract, and no per-media metadata record for BDMA to validate. Consequently, the intended passive-producer boundary is structurally compatible with ADB, but end-to-end DCAM-BDMA compatibility is not demonstrated.

## Requirement traceability

### Architecture and quality attributes

| ID | Requirement/direction | Code and test evidence | Status | Finding |
|---|---|---|---|---|
| ARCH-01 | Java-first Android, XML/ViewBinding, MVVM, LiveData, Gradle | [`app/build.gradle`](../../../app/build.gradle), XML layouts under [`res/layout`](../../../app/src/main/res/layout), `MainViewModel`, `MainUiState` | **Verified** | Current stack matches the development standard. |
| ARCH-02 | UI/controller -> use case -> application boundary -> platform implementation | `MainActivity`, `HardwareButtonRouter`, `MainViewModel`, capture/media/device use cases, `CameraGateway`, `AudioRecorder`, repositories, and platform implementations; enforced by `LayerDependencyTest` | **Verified** | The main capture/device/media flows follow the target direction. |
| ARCH-03 | Core business logic independent of Android/vendor/provider APIs | No Android/AndroidX/Google imports in `core` or feature application/domain; architecture test checks this | **Verified** | Core and feature workflows/contracts can be tested without CameraX or a vendor SDK. |
| ARCH-04 | Modular and maintainable structure | Four enforced top-level package families with vertical `capture`, `media`, and `device` slices | **Partial** | Source ownership and dependency direction are clear; compiler isolation remains limited by the single Gradle module, and cross-feature presentation still sits in one ViewModel/Activity shell. |
| ARCH-05 | Hardware capability behind replaceable implementations | `CameraGateway`, `AudioRecorder`, and `DeviceRepository`; CameraX/MediaRecorder/Android implementations remain outside application/domain | **Partial** | Replacing implementations leaves application/domain code mostly unchanged. Runtime selection, vendor fallback, and device matrix are missing; manifest requires a camera. |
| ARCH-06 | Offline-first core; cloud must not block capture | No cloud call in use cases/repositories/camera output; local storage/logging; token-gated WorkManager upload | **Verified** for current capture path | Video, photo, audio, storage, config, and UI do not require an online service. Metadata/BDMA readiness is still absent, so the entire offline MVP is not complete. |
| ARCH-07 | Provider-neutral cloud/config/update integration | Config uses `ConfigurationSource`; no approved cloud/update use case or boundary exists; the concrete Loggly worker is isolated in logging | **Gap** | Speculative empty interfaces were removed. Cloud/update boundaries should be introduced only with approved behavior; diagnostics remain Loggly-specific. |
| ARCH-08 | Configuration over hardcoding with safe defaults | CSON source/repository and `DcamConfig.defaults`; `storage.mode` selection | **Partial** | Local fallback is good. Camera FHD quality, key mappings, folders, filename format, and provider behavior are prototype hardcoding pending designs. |
| ARCH-09 | Long/blocking work off UI; serialized hardware work | Single-thread media browser executor, Room, WorkManager; CameraX executor/callback contract | **Partial** | Some threading choices conform. No performance/ANR evidence, priority tests, or vendor `HandlerThread` implementation exists. |
| ARCH-10 | Testability through interfaces/fakes | Architecture, storage, browser, input, config, UI-state, DB-manifest, and log-worker unit tests | **Partial** | Boundary tests exist, but capture use cases/repository/camera error flows lack broad fake-based tests and instrumentation coverage is only a placeholder. |
| ARCH-11 | Versioned database evolution | Central Room manifest, exported schemas 1/2, explicit `V1_TO_V2`, DB manifest test | **Verified** for log DB | Database migration discipline exists. It does not provide the required media metadata schema or DCAM-BDMA schema version. |
| ARCH-12 | Security-aware local access and secret handling | Local properties are gitignored; media browser canonicalizes and restricts paths; FileProvider is non-exported and grants temporary access | **Partial** | Useful controls exist. Encryption/key management, update validation, media protection, full permission audit, and security testing are unresolved. |

### MVP behavior and acceptance

| ID | MVP expectation | Current evidence | Status | Missing proof or behavior |
|---|---|---|---|---|
| MVP-01 | Start/stop video and expose recording state | CameraX start/stop/finalize flow; `CaptureState`; foreground-service notification | **Partial** | Real BodyCamera results, 99% target, background/interruption/process-death recovery, and per-file metadata/status. |
| MVP-02 | Capture and save image with state/diagnostics | CameraX `takePicture`, local output, saved/error events, log calls | **Partial** | Real-device success target and per-image metadata/status. |
| MVP-03 | Stable, documented, ADB-readable local structure | Prototype `video`, `SOS`, `image`, `audio` date folders and deterministic names; public DCIM mode | **Partial** | Approved roots/discovery/retention contract and ADB/BDMA validation. App-data mode may not be generally ADB-readable without appropriate access. |
| MVP-04 | Metadata with file/device/time/GPS/status/schema version | Device ID and time are available in separate code paths; no metadata model, use case, or port exists | **Gap** | No general per-media metadata record, association, status vocabulary, GPS, `schema_version`, or authoritative store. |
| MVP-05 | Battery, storage, and GPS availability | `AndroidDeviceRepositoryImpl.readStatus` -> repository -> use case -> UI state | **Verified** for these three status indicators | Broader capability catalog and historical/per-media status are future work. |
| MVP-06 | Missing optional capabilities degrade gracefully | GPS status returns unavailable/disabled/unknown; config falls back to defaults; no-token upload succeeds without network | **Partial** | Systematic camera/microphone/storage/network/GMS/USB capability policy and tests. |
| MVP-07 | Local main-flow diagnostics with rotation/retention and safe context | Logcat, rotating `app.log`, Room outbox/retry/dead archive; capture start/result/error logs | **Partial** | Formal retention/privacy policy, broader categories, secret-redaction tests, and full provider abstraction. |
| MVP-08 | Completed/pending/corrupt/recovered source states | CameraX events update transient `CaptureState` | **Gap** | No persisted media lifecycle or recovery/corruption classification. Log outbox states are unrelated to media lifecycle. |
| MVP-09 | Near-full storage is handled safely | Available bytes are reported | **Gap** | No threshold, preflight, warning/prevention/stop policy, or low-storage recording test. |
| MVP-10 | Valid GPS stored with media when available | GPS hardware/enabled state only | **Gap** | Location acquisition, validity policy, timestamping, and media metadata association. |
| MVP-11 | BDMA discovers/imports/validates/maps media and metadata | Local deterministic prototype paths only | **Gap / external proof needed** | Approved Data Contract, fixture, BDMA importer, ADB E2E test, duplicate/retry/schema-mismatch behavior. |
| MVP-12 | No critical corruption/crashes; record/capture >=99%; BDMA import 100% | Unit tests and successful local build are engineering signals | **External proof needed** | Long-duration real-device, power-loss, near-full storage, performance, crash/ANR, corruption, and BDMA QA results. |

## Current limitations

The architecture is a good foundation, but it should not be interpreted as a completed platform. The following limitations describe the present source, not only missing acceptance paperwork.

### Architecture and maintainability limitations

| Area | Current limitation | Why it matters | Classification |
|---|---|---|---|
| Composition root | `AppComposition` now selects storage, config, logging, device, media, camera, audio, and repository implementations; `MainActivity` retains lifecycle, rendering, and input routing. | Activity coupling is reduced, but adapter selection is still static and `AppComposition` lacks focused composition tests or capability-based selection. | **Improved; incremental follow-up** |
| Camera boundary | `CameraXCameraGatewayImpl` is both a `FrameLayout` visual component and the `CameraGateway` implementation. It owns CameraX binding, recording, capture callbacks, messages, and foreground-service signaling. | UI lifecycle, capture lifecycle, and device implementation cannot evolve independently; headless recovery and vendor SDK substitution become harder. | **Focused refactor before recovery/vendor work** |
| Recording ownership | `RecordingForegroundService` supplies notification/foreground visibility, but `CameraXCameraGatewayImpl`, which is Activity lifecycle-owned, owns the active CameraX `Recording`. | Foreground status alone does not preserve recording across Activity recreation or process death. | **Reliability design and implementation** |
| ViewModel construction | `MainViewModel` receives use cases through `MainViewModelFactory` and remains an app-level cross-feature ViewModel. | It works for one shell, but feature growth can produce a large app-level state object. Feature-specific screens should move state/ViewModel into their feature when lifecycle grows. | **Incremental refactor** |
| Use-case depth | Most capture use cases are still light workflow delegation. | There is currently no application-level location for storage preflight, lifecycle transition rules, metadata finalization, or retry policy. Thin use cases are acceptable now, but policy must not drift into the Activity or platform implementations. | **Future implementation discipline** |
| Build modularity | All packages compile inside one `:app` module. `LayerDependencyTest` checks all production classes for allowed top-level ownership and targeted dependency rules, but Gradle does not enforce module visibility. | Import-level regression risk is reduced; reflection, same-package access mistakes, and untested dependency forms still lack compiler-module enforcement. | **Accept now; reconsider later** |
| Future capability catalog | Cloud, remote config, update, metadata, integrity, security, storage-contract, streaming, and PTT remain roadmap items without source boundaries. | This avoids fake abstraction; each capability still needs an approved use case and domain result/error model before implementation. | **Deferred by design** |
| Threading model | Media browsing, Room, and upload work are offloaded, while CameraX uses its main-executor callback contract. No vendor command queue or measured priority policy exists. | A vendor SDK or long-running stateful operation may require serialization; callback execution and UI work need profiling on real hardware. | **Partial implementation** |

### Product and data limitations

| Area | Current limitation | Consequence |
|---|---|---|
| Requirement authority | Formal functional/non-functional requirements, technical designs, ADRs, and the DCAM-BDMA Data Contract are still missing. | Prototype choices cannot safely become permanent external contracts. |
| Storage contract | Folder names, filename format, public/app-data mode, retention, cleanup, and discovery rules are implementation choices. | BDMA cannot rely on them as an approved compatibility promise; app-data storage may also require special ADB access. |
| Metadata | No authoritative per-media metadata entity/file contains file ID, device ID, capture time, location, source state, and schema version. | BDMA cannot validate or map evidence without guessing, so the core product handoff is incomplete. |
| Media lifecycle | UI state is transient; completed, pending, corrupt, interrupted, and recovered media states are not persisted. | Restart and recovery logic cannot reliably determine whether a file is ready for import. |
| File integrity | There is no checksum, finalize validation, corrupt-file classification, or integrity port/implementation. | Neither DCAM nor BDMA can prove source completeness using an agreed mechanism. |
| Storage safety | Available bytes are displayed, but recording has no documented threshold, preflight decision, reserve, or full-storage transition. | Near-full storage can still cause capture/finalization failure despite status visibility. |
| GPS | The code detects whether GPS exists and is enabled but does not request, validate, timestamp, or associate a location with media. | The MVP GPS metadata requirement is not implemented. |
| Device capability | CameraX is the only camera implementation; physical key codes and FHD selection are fixed prototype behavior; broader camera, microphone, network, GMS, USB, and firmware capabilities are not modeled. | Source abstraction reduces coupling, but compatibility across BodyCamera models is not demonstrated. |
| Configuration | Built-in defaults and local CSON exist; runtime override and remote precedence are absent, and several behavior choices remain hardcoded. | Customer/device variation still requires code changes in some areas. |
| Diagnostics provider | Capture depends on `LogSink`, but the outbox, worker, endpoint construction, and class names are Loggly-specific. | Core capture stays offline-capable, but replacing the online diagnostics provider is not yet a pure configuration change. |
| Database scope | Room currently persists the log outbox only. | Versioned database mechanics exist, but media, metadata, lifecycle, device/user mapping, and recovery records do not. |
| Feature UI | Settings detail pages are read-only prototypes and most menu features remain placeholders. | Navigation demonstrates structure, not completed feature behavior or persistence. |
| Security | Encryption, key ownership/rotation, media protection, update verification, sensitive metadata rules, and BDMA decryption compatibility are unresolved. | Security readiness cannot be inferred from package isolation or secret hygiene alone. |

### Verification limitations

- The 19 local unit tests cover useful boundaries, including four architecture rules, but not the complete capture repository/use-case flow, CameraX behavior, lifecycle recovery, low storage, GPS, permissions, database migration execution, or BDMA integration.
- There is no instrumentation or automated real-device UI/camera suite.
- A successful debug build and lint run prove build health, not recording reliability, corruption resistance, battery behavior, performance, or security.
- No audited evidence demonstrates the `>=99%` recording/image target, `100%` BDMA import target, zero critical corruption, or zero critical main-flow crashes.
- No checked-in device/firmware/Android matrix proves operation across the intended BodyCamera range.

## Future improvement directions

Improvements should preserve the existing dependency direction and proceed from contract and reliability work toward optional features. A wholesale rewrite or immediate framework migration is not indicated.

### Direction 1: establish contracts before expanding implementation

1. Approve the DCAM-BDMA Data Contract: roots, discovery, filenames, metadata fields/types, schema version, state vocabulary, association, checksum, duplicate/retry handling, and read/write ownership.
2. Write the storage, metadata, recording lifecycle/recovery, GPS validity, and security designs.
3. Record decisions that materially constrain adapters or data using ADRs, especially camera API/fallback, authoritative metadata store, encryption, and process-recovery ownership.
4. Convert the consolidated baseline into traceable requirement IDs and acceptance tests shared with BDMA and QA.

This prevents the prototype folder layout, speculative source contracts, or current provider choices from becoming accidental contracts.

### Direction 2: build the reliability and data foundation

1. Introduce an authoritative per-media domain record with identity, type, device/operator context, timestamps, optional valid GPS, lifecycle state, schema version, and integrity information defined by the contract.
2. Persist lifecycle transitions before and after fragile file operations so restart recovery can distinguish recording, pending-finalization, completed, corrupt, and recovered artifacts.
3. Add storage preflight and reserve policy before capture; define safe behavior when capacity changes during recording.
4. Finalize media and metadata as one recoverable workflow, even if the underlying filesystem cannot provide a single atomic transaction.
5. Define a location boundary from the approved metadata use case, with explicit unavailable, stale, invalid, and valid outcomes; GPS failure must not abort capture.
6. Add integrity verification and recovery scanning before marking data import-ready.

The exit condition is not merely “files are created”; it is “each artifact has a durable, explainable, BDMA-consumable state after normal completion or interruption.”

### Direction 3: make runtime composition and hardware substitution clearer

1. **Completed structurally:** `AppComposition` now owns concrete dependency creation while `MainActivity` retains Android lifecycle and rendering. Add fake/capability-driven composition tests before claiming runtime substitutability.
2. Separate the preview widget from the camera/capture driver so recording ownership is not inherently tied to a View.
3. Decide whether the foreground service or another lifecycle owner should own long-running recording and recovery, then implement that design explicitly.
4. Provide adapter selection by capability/configuration for CameraX, vendor SDK, and unsupported/fake implementations.
5. Move device-specific key mapping and camera profiles behind validated device capability/configuration adapters.
6. Add contract tests that every camera/device adapter must pass.

This is targeted refactoring around composition and lifecycle—not a rewrite of domain, repository, or use-case layers.

### Direction 4: complete provider and configuration independence

1. Define a provider-neutral asynchronous diagnostics upload boundary below `LogSink`.
2. Keep local-only/no-op behavior as a first-class implementation, then place Loggly behind a provider adapter.
3. Implement the documented configuration precedence only after ownership and validation rules are approved: runtime -> remote -> local -> defaults.
4. Reject unsafe remote values locally and preserve capture when remote config, cloud, GMS, or Internet is unavailable.
5. Add update/cloud implementations only behind approved capability contracts; do not route them through capture workflows.

### Direction 5: grow features without weakening boundaries

- Give each approved settings/feature area its own ViewBinding screen, feature state/ViewModel, use cases, and domain contracts where justified.
- Keep physical keys and touch actions converging on application commands rather than separate hardware-only business flows.
- Add Device/User foundations before streaming and PTT so identity and authorization do not get embedded in provider SDK callbacks.
- Do not add speculative boundaries; define them from an approved use case and expected domain result/error model.
- Keep the existing single Gradle module until measured coupling, build performance, parallel ownership, or enforcement needs justify extraction. The feature-first package boundaries can become future Gradle boundaries without another conceptual reorganization.

### Direction 6: turn quality targets into repeatable evidence

| Test/evidence layer | Needed improvement |
|---|---|
| Unit | Fake-boundary tests for use cases/repositories, media state transitions, storage thresholds, GPS validity, config precedence, integrity, and error mapping. |
| Database | Room migration tests from every supported version plus crash/restart consistency tests for media lifecycle data. |
| Adapter/instrumentation | CameraX/vendor contract tests, permissions, Activity recreation, foreground/background behavior, process kill, and FileProvider/ADB exposure. |
| Integration | Shared DCAM-BDMA fixtures for valid, duplicate, partial, corrupt, unknown-schema, interrupted, and repeated-import cases. |
| Hardware | A maintained device/firmware/Android capability matrix and repeated capture, long recording, low-battery, low-storage, GPS, offline, weak-network, reboot, and thermal tests. |
| Quality/security | Startup/capture/storage latency, CPU/memory/battery/ANR measurement, secret/redaction checks, update validation, media protection, and threat review. |

## Refactor timing guidance

| Timing | Recommended action |
|---|---|
| **Now / before major feature work** | Define approved data/lifecycle contracts; add fake-based workflow and composition tests; keep new policy out of UI and adapters. (`AppComposition` extraction is complete.) |
| **Before vendor camera or recovery work** | Separate preview UI from capture ownership and decide the long-running recording lifecycle owner. |
| **Before adding another diagnostics provider** | Introduce a neutral uploader/local-only boundary around Loggly-specific code. |
| **When metadata/storage contracts are approved** | Implement persistent media records, state transitions, integrity, GPS association, and BDMA fixtures through the existing port/adapter direction. |
| **Later, if measurable pressure appears** | Split Gradle modules or introduce a DI framework. Neither is required merely to make the current architecture respectable. |
| **Do not do yet** | Rewrite working layers, invent ports for unapproved future capabilities, or modularize every package without an ownership/build/enforcement reason. |

## Priority gaps and recommended evidence

| Priority | Required next outcome | Evidence that closes the gap |
|---|---|---|
| P0 | Approve DCAM-BDMA Data Contract and storage/metadata design | Versioned schema, fixtures, discovery rules, media association, lifecycle/status rules, duplicate/retry behavior, and joint DCAM-BDMA tests. |
| P0 | Persist media lifecycle and recover interrupted recordings | State-machine tests plus app-kill, reboot/power-loss, corrupt/partial-file, and recovery results on BodyCamera. |
| P0 | Implement storage safety | Configured threshold/policy, preflight checks, safe finalize/stop behavior, and near-full/exhausted-device tests. |
| P0 | Validate target hardware | Device/firmware/Android capability matrix with record, photo, audio, key, storage, lifecycle, and permission results. |
| P1 | Implement valid GPS-to-metadata flow | Fake-location unit tests and unavailable/stale/valid real-device scenarios. |
| P1 | Make adapter selection capability-driven | `AppComposition` exists; add CameraX/vendor/unsupported selections plus adapter contract tests. |
| P1 | Generalize diagnostics provider boundary | Provider-neutral upload contract, local-only implementation, Loggly adapter, and offline/failure tests. |
| P1 | Expand automated coverage | Fake-port tests for use cases/repositories, CameraX instrumentation, Room migration tests, and UI/permission/lifecycle tests. |
| P2 | Consider multiple Gradle modules | ADR based on measured build/team coupling; preserve the established app/core/feature/platform dependency direction if extraction becomes justified. |

## Verification record

Static verification performed during this audit:

- Confirmed a single `:app` Gradle module and enumerated 67 production Java files, 11 local unit-test files, and 19 test methods.
- Confirmed `core` and feature application/domain do not import Android, AndroidX, Google/provider APIs, `app`, or `platform`; confirmed platform does not import the app shell.
- Reviewed composition, capture, storage, config, device-status, logging/outbox, Room migration, physical-key, and media-browser paths.
- Recorded the evidence against the refactored working tree based on commit `9857cea`; the structural changes are intentionally uncommitted during this audit.

Build verification command:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Result: **PASS** on 2026-07-06; all 19 local unit tests passed and the debug APK assembled successfully. Automated unit/build success is necessary engineering evidence, but it does not substitute for BodyCamera, BDMA, performance, reliability, security, or QA acceptance evidence.

Android lint verification:

```powershell
.\gradlew.bat lintDebug
```

Result: **PASS** on 2026-07-06 with no lint errors. Lint reported 23 non-blocking warnings (primarily compound-drawable and hardcoded-text suggestions); these are quality backlog items rather than proof of MVP acceptance.

## Final assessment

The project is on a sound architectural path: dependency direction is visible, tested, and practical; Android/provider behavior is mostly kept at the edges; the core capture path is local-first; and extension points exist for additional hardware and services.

The precise statement supported by evidence is:

> **The current source structure satisfies the main modularity, dependency-isolation, extensibility, and offline-first architecture expectations. The implementation does not yet satisfy the complete DCAM MVP requirements or acceptance criteria because its metadata, lifecycle/integrity, storage-safety, GPS, BDMA, recovery, hardware-validation, and quality evidence are incomplete.**
