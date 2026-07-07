# System and application architecture

## Architecture intent

The Architecture Home is **Approved 1.11** (Confluence page version 15, updated 2026-07-06). It identifies the approved Data Contract as the official baseline for storage/data/integration details. The Requirements set now also includes Approved Android Device Operation Requirements 1.0.

DCAM is an offline-first, modular, hardware-aware Android application. Core business logic should remain independent of Android APIs, BodyCamera vendor SDKs, camera SDKs, and cloud providers.

The current high-level context is:

```text
Hardware / Android platform
    Camera, microphone, GPS, storage, battery, network, USB
        -> through platform adapters
DCAM application
    UI/controller -> use case -> application boundary <- platform implementation
        ->
Local media / metadata / DB / logs
        -> ADB read initiated by desktop
BDMA Desktop

Optional cloud/config/diagnostics providers sit behind application-owned output ports and may be absent.
```

## Mandatory architectural principles

- **Offline first:** recording, capture, local storage, metadata, logging, basic device status, and BDMA-readiness do not require Internet or cloud.
- **Reliability first:** data preservation and explicit failure state take priority over secondary work or visual polish.
- **Capability based:** detect what the specific BodyCamera can do; do not assume all models, Android versions, firmware, GMS, sensors, or storage behave alike.
- **Platform abstraction:** business logic must not call Android/vendor/cloud SDKs directly.
- **Cloud-provider abstraction:** Firebase may be an implementation but is not the architecture.
- **BDMA-compatible by design:** source data is deterministic, versioned, and contract-driven.
- **Configuration over hardcoding:** device/customer/environment-sensitive behavior comes from safe config layers.
- **Observability:** important flows expose meaningful state and diagnostics.
- **Backward compatibility:** metadata and Data Contract evolution should be versioned.
- **Security awareness:** least permission, secret hygiene, controlled logs, protected update/config/data.

## Standard layers and dependency direction

| Layer | Responsibility | Forbidden dependency/behavior |
|---|---|---|
| Domain | Entities, immutable values, enums, and pure business rules | Android, UI, database, filesystem, vendor, or provider APIs |
| Application | Use cases plus application-owned capability/repository boundaries | Android/framework APIs and concrete implementations |
| Interface Adapter | Translate UI/storage/provider input/output and implement application boundaries | Core product policy and direct dependency from application/domain |
| Frameworks & Drivers | Android, CameraX, MediaRecorder, Room, filesystem, network, vendor SDK | Product policy that belongs in domain/application |

The source uses feature-first Clean Architecture with Ports and Adapters. MVVM is the presentation pattern at the UI boundary; it does not replace or conflict with the four dependency layers. A Java `interface` is placed by ownership: use-case interfaces live in `application/usecase`; repository/hardware/provider boundaries live in `application/port`; concrete implementations live in `platform` or an application repository package. The folder keeps the architecture term `port`, but class/file names use capability names such as `CameraGateway`, not a `Port` suffix.

## Current source organization

| Package family | Responsibility |
|---|---|
| `app` | Android entry point, navigation, cross-feature presentation, and composition root |
| `feature/<name>/domain` | Feature-owned entities and pure rules |
| `feature/<name>/application` | Use cases and application-owned capability/repository boundaries |
| `feature/<name>/presentation` | Feature-owned UI state/ViewModel when a feature needs its own presentation |
| `platform` | Android, hardware, storage, database, and provider adapters |
| `core` | Deliberately shared capabilities using the same domain/application vocabulary |

The project currently remains one Gradle `:app` module. Package boundaries are enforced by source-level architecture tests; a future Gradle split requires a separate measured justification.

## Boundary and capability rule

Current approved boundaries include use cases such as `PhotoCaptureUseCase`, `VideoRecordingUseCase`, `AudioRecordingUseCase`, `CaptureEventUseCase`, `BrowseMediaUseCase`, `OpenMediaUseCase`, and capability/repository boundaries such as `CameraGateway`, `AudioRecorder`, `DeviceRepository`, `MediaRepository`, `MediaOpener`, `ConfigurationSource`, `ConfigurationRepository`, `LanguagePreferenceStore`, and `LogSink`. Names describe the application capability, never the current library or vendor. Concrete implementations end with `Impl`, for example `CameraXCameraGatewayImpl` and `DcamLogSinkImpl`.

Do not reserve future architecture with empty `*Service` interfaces. Location, metadata, integrity, cloud, update, streaming, PTT, and other future capabilities enter source only after an approved use case defines domain inputs/results/errors and demonstrates the need for a replaceable boundary. Do not add a generic event bus, domain-event publisher, or cross-feature application-event dispatcher until a concrete metadata/media lifecycle/cloud workflow needs decoupled side effects. Each BodyCamera or provider-specific implementation belongs behind a platform implementation. A vendor SDK change should primarily change that implementation, not UI, ViewModel, use cases, or domain.

## Threading model

- Stateful, fragile, call-order-sensitive hardware/vendor SDK operations should use a dedicated `HandlerThread` or the SDK-required serialized thread.
- File I/O, parsing, checksums, ordinary database work, and background helpers use `ExecutorService` or a library-owned executor.
- Retrofit/network uses its managed execution unless a specific helper needs another executor.
- UI state returns to the main thread through ViewModel/LiveData.
- Logging, cloud, metrics, and background work may not degrade recording.

## Android compatibility strategy

- Documentation direction: Android 7.0+ if device/SDK permits; final SDK policy and device matrix TBD.
- Camera direction: CameraX first, Camera2/vendor fallback after real-device POC.
- Centralize permission handling for camera, microphone, location, storage/media, network, notifications, and version-specific behavior.
- Support GMS and non-GMS devices.
- Treat GMS as a capability relevant to some SDKs, not a requirement for all cloud access.
- REST/HTTPS and BDMA/backend-side cloud work can operate without Android GMS.
- Gracefully degrade when GPS, network, cloud, or other optional capability is unavailable.

## Architecture decision status

Decided direction: Java-first, offline-first, capability-based operation, feature-first Clean Architecture, application-owned ports/platform adapters, serialized hardware access where needed, cloud abstraction, BDMA compatibility, ADB boundary, and DCAM-producer/BDMA-consumer ownership.

Decided by Data Contract 1.2: logical storage layout, Internal/External/Auto selection, media formats and naming, `_IMP`/`_enc` suffixes, MP4-only MD5 behavior, config/database/log permissions, BDMA import results, and post-import cleanup.

Still pending: physical device paths, exact embedded metadata and SQLite schemas, DB concurrency/write protocol, final camera API, future Gradle-module split, source lifecycle/error model, duplicate/retry recovery behavior, streaming/PTT protocols, encryption/key management/decryption detail, and detailed update mechanism.
