# System and application architecture

## Architecture intent

DCAM is an offline-first, modular, hardware-aware Android application. Core business logic should remain independent of Android APIs, BodyCamera vendor SDKs, camera SDKs, and cloud providers.

The current high-level context is:

```text
Hardware / Android platform
    Camera, microphone, GPS, storage, battery, network, USB
        ↓ through platform adapters
DCAM application
    UI → ViewModel → UseCase → Repository → service interfaces
        ↓
Local media / metadata / DB / logs
        ↓ ADB read initiated by desktop
BDMA Desktop

Optional cloud/config/diagnostics providers sit behind interfaces and may be absent.
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

## Target layers and dependency direction

| Layer | Responsibility | Forbidden dependency/behavior |
|---|---|---|
| UI | Render state and handle input | Business logic, direct SDK/file/network calls |
| ViewModel | Hold UI state, invoke use cases, expose LiveData | Hardware/cloud/vendor APIs, heavy work |
| UseCase | Implement application workflow and preconditions | Activity/Fragment/View and provider SDKs |
| Repository | Coordinate service interfaces and map domain data | Vendor SDKs and UI behavior |
| Service Interface | Describe the capability the app needs | Leaking vendor-specific models upward |
| Platform Adapter/Data Source | Implement interface through Android/vendor/cloud APIs | Complex product policy |

The architecture page calls MVVM + UseCase + Repository “proposed / ADR needed”; the Android Development Standard treats MVVM and the service/adapter rules as the project implementation standard. Until an ADR resolves this wording, follow the stricter standard for new production code.

## Proposed modules

| Module | Responsibility |
|---|---|
| `ui` | Screens and presentation state |
| `camera` | Camera capability/adapters |
| `recording` | Recording flow and state |
| `capture` | Image-capture flow |
| `metadata` | Metadata generation and validation |
| `storage` | Local folders, files, records, and exposure |
| `gps` | Location abstraction |
| `device` | Device info, health, and capability detection |
| `user` | User/operator/role foundation (Phase 2) |
| `logging` | Local logs and diagnostics adapters |
| `config` | Runtime/remote/local/default configuration |
| `cloud` | Provider-neutral cloud capability |
| `update` | Play/self/manual/auto update adapters |
| `common` | Small shared domain primitives/interfaces |

Final package and Gradle-module boundaries are TBD.

## Platform service catalog

Expected interfaces include `CameraService`, `CaptureService`, `AudioService`, `LocationService`, `StorageService`, `MetadataService`, `DatabaseService`, `DeviceService`, `CloudService`, `RemoteConfigProvider`, `UpdateService`, `LogService`, `SecurityService`, `PTTService`, `StreamingService`, and `FileIntegrityService`.

Each BodyCamera or provider-specific implementation belongs behind an adapter. A vendor SDK change should primarily change its adapter, not UI, ViewModel, use cases, or repositories.

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

Decided direction: Java-first, offline-first, capability-based operation, service interfaces/platform adapters, serialized hardware access where needed, cloud abstraction, BDMA compatibility, ADB boundary, and DCAM-producer/BDMA-consumer ownership.

Still pending: final camera API, metadata/schema, folder structure, database strategy, package/module structure, error/state model, streaming/PTT protocols, encryption/key management, and detailed update mechanism.
