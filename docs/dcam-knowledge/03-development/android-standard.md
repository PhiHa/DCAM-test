# Android development standard

Source status: **Approved 1.7**, Confluence page version 10, updated 2026-07-08.

## Target stack from the documentation

| Concern | Standard/direction |
|---|---|
| Language | Java-first |
| UI | XML layouts + ViewBinding |
| Architecture | Feature-first Clean Architecture + Ports and Adapters; MVVM at the UI boundary |
| UI state | LiveData |
| Ordinary background work | ExecutorService |
| Stateful hardware/vendor SDK calls | HandlerThread or SDK-required serialized thread |
| REST/API | Retrofit if required |
| JSON | Gson or Jackson; undecided |
| Database | SQLite direction; Room vs direct strategy still needs contract/design confirmation |
| Dependency injection | Not mandatory; introduce only when complexity justifies it |
| Build | Gradle |

## Non-negotiable dependency rule

```text
Activity/Fragment/controller
    -> application use case
    -> application boundary (for example Repository, CameraGateway)
    <- platform/application implementation
    -> Android/vendor/cloud framework
```

Dependency direction always points inward. UI, ViewModel, and use-case code must not call camera, hardware, storage, Retrofit, Firebase, or other provider SDKs directly. Application code depends only on domain values and boundaries it owns. Platform implementations translate external behavior into those boundaries.

`Interface Adapters` is a Clean Architecture layer name; it does not mean a folder for Java `interface` declarations. Public feature/core interfaces live only in `application/usecase` or `application/port`, and must represent a real use-case or capability boundary. The package may be named `port`, but class/file names do not use the `Port` suffix. Use capability names such as `CameraGateway`, `AudioRecorder`, `MediaOpener`, `LanguagePreferenceStore`, `ConfigurationSource`, and `LogSink`; keep `Repository` for repository contracts. Concrete implementations must end with `Impl`.

Canonical feature package shape:

```text
feature/<feature>/
    domain/
    application/
        usecase/
        port/
        repository/
    presentation/
```

## Threading rules

| Work | Expected mechanism |
|---|---|
| Camera open/close/start/stop | Dedicated HandlerThread or SDK-required executor |
| Stateful BodyCamera/vendor command | HandlerThread unless vendor requires another thread |
| PTT/streaming SDK command | Serialized SDK-required thread |
| File/metadata/checksum work | ExecutorService |
| Database operation | Database/Room executor or ExecutorService |
| Retrofit request | Retrofit-managed execution |
| UI update | Main thread via ViewModel/LiveData |

Do not put fragile hardware commands into a general thread pool. Do not expose raw vendor callbacks or status objects to UI.

## State and error rules

- ViewModel exposes clear, UI-oriented states such as idle, preparing, recording, stopping, completed, and error.
- Map raw SDK exceptions/status codes at adapter boundaries into application/domain results.
- Use stable error categories: permission, unsupported capability, SDK, storage, metadata, network/cloud, and unknown/diagnostic.
- Do not throw raw vendor exceptions through multiple layers.
- Important failures carry enough safe context for diagnostics.

## Storage, metadata, and cloud rules

- UI/ViewModel never knows physical folder paths or writes business files directly.
- Use cases depend on domain models, not filesystem details.
- Temporary/final data must be distinguishable.
- The approved Data Contract governs logical roots, folders, media naming, MD5 scope, BDMA permissions, import results, and cleanup. The future Storage/Database/Security designs must fill its explicitly open implementation details.
- Cloud calls go through provider-neutral output ports defined from approved use cases.
- Core recording/capture/storage/metadata and BDMA readiness remain fully local-capable.

## Pull-request checklist

- No direct SDK access from UI, ViewModel, or use case.
- Application logic depends on owned boundaries; vendor/provider implementation remains in platform/application implementation classes.
- Stateful hardware access is serialized.
- Long work does not block UI.
- UI state is observable and domain-oriented.
- SDK errors are mapped.
- Critical success/error paths are logged without sensitive data.
- Cloud is abstracted and optional.
- Storage/metadata details do not leak upward.
- Tests use fakes at application boundaries where practical.
- Jira issue is linked when applicable.

## Training context

The team is expected to come from Java/JavaFX/Desktop and BDMA knowledge. The two-week onboarding covers Android Studio/Gradle/ADB, Java-on-Android mindset, Activity/Service/Foreground Service lifecycle, permissions, CameraX/Camera2, storage, GPS, network basics, Logcat/debugging, and deployment to real BodyCamera hardware.

Training exit means each developer can build/install/debug the app, run capture and recording samples, handle runtime permissions, perform basic storage I/O, understand lifecycle/background constraints, and work through the Git/Jira workflow.

## Architecture evolution: current decisions and future triggers

Keep the current single-module, single-composition design while it remains easy to scan. Do not add abstractions only to match a future architecture diagram.

Current ownership rules:

- `MainActivity` owns the Activity-scoped capture runtime and hardware command routing.
- A retained `MainViewModel` may retain UI state, but it must rebind to the current Activity's capture event source and must not retain an old Activity-scoped camera/audio runtime.
- `RecordingForegroundService` currently provides foreground visibility only. It does not own or recover recording.

Future work must be triggered by a concrete requirement:

| Trigger | Then consider |
|---|---|
| Recording must survive Activity loss or configuration recreation | Move recording ownership to a durable runtime/foreground service and define recovery behavior |
| Streaming, recording, and preview need the camera concurrently | Separate camera runtime ownership from preview attachment |
| A second vendor/model needs different hardware behavior | Add a capability-specific platform implementation selected in composition |
| A feature gains independent loading, error, navigation, or background state | Give that feature its own presentation state/ViewModel |
| `AppComposition` becomes difficult to scan or owns multiple independent runtime graphs | Delegate construction to feature-level composition helpers |
| Configuration develops several cohesive groups | Introduce typed configuration sections for those real groups |
| Package tests no longer prevent accidental coupling at team scale | Evaluate Gradle modules for compile-time boundaries |

Avoid a generic event bus, generic device adapter, empty vendor packages, placeholder ViewModels, or a custom scope framework without those triggers.
