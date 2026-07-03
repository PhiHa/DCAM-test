# Android development standard

## Target stack from the documentation

| Concern | Standard/direction |
|---|---|
| Language | Java-first |
| UI | XML layouts + ViewBinding |
| Architecture | MVVM |
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
Activity/Fragment + ViewBinding
    ↓ observes
ViewModel + LiveData
    ↓ calls
UseCase
    ↓ depends on
Repository interface/implementation
    ↓ depends on
Service Interface
    ↓ implemented by
Platform/Vendor/Cloud Adapter
```

UI, ViewModel, and UseCase code must not call camera, hardware, storage, Retrofit, Firebase, or other provider SDKs directly. Repositories coordinate application services but must depend on service interfaces rather than vendor implementations.

Service interfaces describe what DCAM needs. Platform adapters translate Android/vendor/provider behavior, threading, errors, and data into domain-facing results.

## Threading rules

| Work | Expected mechanism |
|---|---|
| Camera open/close/start/stop | Dedicated HandlerThread |
| Stateful BodyCamera/vendor command | HandlerThread unless vendor requires another thread |
| PTT/streaming SDK command | Serialized SDK-required thread |
| File/metadata/checksum work | ExecutorService |
| Database operation | Database/Room executor or ExecutorService |
| Retrofit request | Retrofit-managed execution |
| UI update | Main thread via ViewModel/LiveData |

Do not put fragile hardware commands into a general thread pool. Do not expose raw vendor callbacks or status objects to UI.

## State and error rules

- ViewModel exposes clear, UI-oriented states such as idle, preparing, recording, stopping, completed, and error.
- Map raw SDK exceptions/status codes at the adapter and repository/domain boundaries.
- Use stable error categories: permission, unsupported capability, SDK, storage, metadata, network/cloud, and unknown/diagnostic.
- Do not throw raw vendor exceptions through multiple layers.
- Important failures carry enough safe context for diagnostics.

## Storage, metadata, and cloud rules

- UI/ViewModel never knows physical folder paths or writes business files directly.
- UseCase should depend on domain models, not filesystem details.
- Temporary/final data must be distinguishable.
- The future Storage Design and Data Contract override prototype folder/schema choices.
- Cloud calls go through provider-neutral interfaces.
- Core recording/capture/storage/metadata and BDMA readiness remain fully local-capable.

## Pull-request checklist

- No direct SDK access from UI, ViewModel, or UseCase.
- Repository depends on interfaces; vendor implementation remains in an adapter.
- Stateful hardware access is serialized.
- Long work does not block UI.
- UI state is observable and domain-oriented.
- SDK errors are mapped.
- Critical success/error paths are logged without sensitive data.
- Cloud is abstracted and optional.
- Storage/metadata details do not leak upward.
- Tests use fakes at service boundaries where practical.
- Jira issue is linked when applicable.

## Training context

The team is expected to come from Java/JavaFX/Desktop and BDMA knowledge. The two-week onboarding covers Android Studio/Gradle/ADB, Java-on-Android mindset, Activity/Service/Foreground Service lifecycle, permissions, CameraX/Camera2, storage, GPS, network basics, Logcat/debugging, and deployment to real BodyCamera hardware.

Training exit means each developer can build/install/debug the app, run capture and recording samples, handle runtime permissions, perform basic storage I/O, understand lifecycle/background constraints, and work through the Git/Jira workflow.
