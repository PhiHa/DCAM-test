# DCAM Android source structure

This package follows the DCAM Android Development Standard with manual dependency composition in `MainActivity`.

```text
presentation/                Activity-facing state and ViewModel
    ↓
application/command,usecase/ Product commands and workflow entry points
    ↓
domain/model,repository,service,event/
                             Android/provider-independent contracts
    ↑ implemented by
data/repository/             Repository coordination and local config
camera/, audio/, device/, storage/, logging/
                             Current Android/CameraX/MediaRecorder/Room adapters
platform/recording/          Android foreground recording lifecycle support
data/db/                     Room database boundary and migrations
```

Dependency rules:

- `domain` must not import Android, CameraX, Room, WorkManager, or provider SDKs.
- `application` depends on domain repositories only.
- `presentation` issues application commands and renders immutable `MainUiState` from LiveData.
- Repositories depend on `domain.service` interfaces, not CameraX or MediaRecorder implementations.
- Platform/provider callbacks are mapped to domain events before presentation observes them.
- Empty Phase 2/3 service interfaces are deliberate placeholders. Add methods only after the relevant Data Contract, Technical Design, or ADR is approved.

Current transitional boundary:

- `DcamMediaOutput` still exposes CameraX output types inside the storage/camera adapter layer. It must not be passed into application or domain code.
- Camera, menu, and placeholder shells use dedicated XML/ViewBinding layouts. Feature-specific placeholder screens should receive their own layouts and ViewModels when requirements are approved.
- `RecordingForegroundService` provides Android foreground visibility/process priority, but the CameraX adapter still owns the active recording. Full recovery/process-death behavior requires the pending recording design.
