# Build 0.1 critical fix checklist

- Status: active
- Execution rule: complete and verify one item before starting the next item.

This checklist covers the smallest fixes needed for the Working Recording Slice. A completed item
must include focused automated tests and must not silently expand into a Build 0.2 feature.

| Order | Fix | Status | Completion evidence |
|---:|---|---|---|
| 1 | Default media encryption to off while preserving an explicit enabled configuration | Complete | `gradlew test assembleDebug` passed on 2026-07-10 |
| 2 | Add one serialized recording coordinator for UI, hardware and camera events | Complete | Focused transition tests and `gradlew test assembleDebug` passed on 2026-07-10 |
| 3 | Route video and image output through Temp/staging instead of the final Media path | Complete | Focused staging-path tests and `gradlew test assembleDebug` passed on 2026-07-10 |
| 4 | Add storage-capacity prechecks and safe storage-full handling | Complete | Focused resolver/capacity/failure tests and `gradlew test assembleDebug` passed on 2026-07-11 |
| 5 | Close, validate and safely publish Temp/staging media to its final path | Complete | Focused publication/recovery tests and `gradlew test assembleDebug` passed on 2026-07-11 |
| 6 | Persist the minimal media/finalization state and mark `BDMA_READY` only after file and DB success | Replaced | Build 0.1 uses safe publication into `Media/*` as its filesystem readiness boundary; per-media DB schema/retention is deferred |
| 7 | Add bounded DB-busy retry/timeout behavior for finalization | Not applicable | Finalization has no Build 0.1 DB dependency after the item 6 decision |
| 8 | Move recording lifetime out of the Activity lifecycle boundary | Complete | Activity recreation test plus `gradlew test assembleDebug` passed on 2026-07-13 |
| 9 | Recover or preserve artifacts after process death during recording/finalization | Pending | 2026-07-13 ADB rerun recovered an 11 s force-stop sample and an 11 s reboot sample; physical remount/repetition/power-cut proof remains |
| 10 | Prove the Build 0.1 sample flow with BDMA through ADB | Pending | 30-second video and image are finalized, detected and imported |

## Completed item: 1 — encryption default

Scope:

- `DcamConfig` defaults video encryption to `false`.
- Missing legacy/prefixed CSON encryption keys resolve to the same false default.
- An explicit `file.encryption="1"` or `video.file.encryption="1"` continues to enable encryption.
- Existing persisted SharedPreferences are not overwritten by this default-only change.

Verification:

- Core and app configuration tests pass.
- Full local unit-test suite passes.
- Debug APK builds successfully.

## Completed item: 2 — serialized recording coordinator

Scope:

- `SerializedRecordingCoordinator` is the single `VideoRecordingUseCase` supplied to UI and
  hardware routing.
- The same coordinator is the `CaptureEventUseCase` supplied to CameraX.
- One FIFO queue owns application recording decisions for start, stop, SOS handoff and camera
  completion/failure events.
- Duplicate start and stop requests are rejected before reaching the camera adapter.
- The camera boundary is bound exactly once by the composition root.

Verification:

- Focused tests cover duplicate start, ordered start/stop, camera-event ordering, completion followed
  by a new start, SOS handoff and single camera binding.
- Full local unit-test suite passes.
- Debug APK builds successfully.

Limitations intentionally left for later checklist items:

- Temp/staging was outside item 2 and is completed separately in item 3.
- Storage thresholds, transactional finalization, Activity-independent lifetime and process recovery
  are not part of this item.

## Completed item: 3 — Temp/staging output

Scope:

- Normal video, SOS/IMP video and image capture targets are created under `Temp`, outside the
  BDMA-facing `Media` folders.
- Camera completion no longer scans or publishes staged visual media.

Verification:

- Focused storage tests cover normal video, SOS/IMP video and image staging paths under the
  selected app-specific root and prove staging is absent from final media browsing.
- Full local unit-test suite passes.
- Debug APK builds successfully.

Limitations intentionally left for later checklist items:

- Closing, validating and publishing staging media to final `Media` paths remains item 5.
- Filesystem finalization is item 5; Activity-independent lifetime and destructive recovery proof
  remain items 8 and 9.

## Completed item: 4 — storage selection and capacity handling

Scope:

- The operator storage policy is `Internal`, `External` or `Auto`; the fresh-install default is
  `Auto` and the Storage settings surface persists changes.
- `Internal` keeps the primary Android app-specific root. `External` and `Auto` select the first
  capture-qualified removable app-specific root, then fall back to Internal before a new capture if
  no external root is mounted, writable and above the start threshold.
- Root selection is fixed for the active file. Storage removal or exhaustion never triggers a
  mid-file root switch.
- All media starts use the same resolved root and capacity precheck.
- The Build 0.1 start threshold is an estimated 30-minute 10 Mbps recording plus a 500 MiB
  finalization reserve.
- CameraX recording receives a file-size limit that preserves the reserve. Insufficient-storage and
  size-limit finalization errors are classified as storage failures and preserve the staged file.
- Device-status free bytes now report the resolved capture root.

Verification:

- Resolver tests cover external priority, unqualified-external fallback, explicit Internal, and
  explicit External behavior.
- Capacity tests cover unavailable, unwritable, below-threshold and exact-threshold decisions plus
  the finalization reserve.
- Failure tests cover CameraX insufficient-storage and file-size-limit classification.
- Storage-setting and path tests cover selection behavior and one resolved root across media types.
- Full local unit-test suite passes.
- Debug APK builds successfully.

Limitations intentionally left for later checklist items:

- Root mapping and sustained-write qualification still require the real BodyCamera/ADB POC.
- Fallback is evaluated for new capture only; active files fail safely on their selected root.
- Closing, validating and publishing staging media is completed in item 5.

## Completed item: 5 — filesystem finalization

Scope:

- Successful visual-media callbacks publish from `Temp` through a hidden non-contract file in the
  final directory, flush it with `FileDescriptor.sync()`, verify its size, and rename it into the
  approved `Media/*` path without overwriting an existing file.
- Publication runs on one background executor; recording completion and an SOS handoff wait for the
  publication result.
- Publication failure leaves staging intact and removes any incomplete hidden publication copy when
  possible.
- Once per process startup, Android decoder validation recovers playable unencrypted contract-named
  candidates. Invalid, encrypted, duplicate or ambiguous candidates remain in `Temp`.

Verification:

- Focused tests cover successful publication, empty staging, existing-final conflict, playable
  startup recovery, invalid-media preservation, encrypted-media preservation and final/staging
  duplicate preservation.
- Full local unit-test suite passes.
- Debug APK builds successfully.

## Replaced item: 6 — no per-media DB gate in Build 0.1

The BDMA Data Contract discovers media by scanning approved `Media/*` folders and ignores `Temp`.
For the Working Recording Slice, successful filesystem publication is therefore the observable
readiness boundary. The proposed `media_session` / `media_finalization_state` schema is deferred
until its exact schema, consumer, audit purpose and bounded retention policy are approved. Item 7
does not apply to this finalization path.

CameraX 1.6.1 uses the interruption-resilient AndroidX Media3 muxer. Official source evidence and
the required destructive BodyCamera acceptance procedure are recorded in
[`camerax-interrupted-recording-recovery.md`](../evidence/camerax-interrupted-recording-recovery.md).
Target-device process-kill, reboot/power-loss, removable-media and duration-loss measurements remain
part of item 9 and the final ADB gate.

Initial BWC evidence now proves one force-stop recording recovered and published at 30.104 seconds,
and one reboot recording remained preserved with 27.400 seconds (about 2.6 seconds trailing loss).
The rebooted device USB-shares removable storage, so mount-event recovery, repeated trials and a true
power-cut test remain open. See the evidence page for commands, measurements and discovered Android
API compatibility fixes.

## Completed item: 8 — Activity-independent recording lifetime

Scope:

- `AppComposition` is retained at app scope instead of recreated per Activity instance.
- One app-scoped `SerializedRecordingCoordinator` owns recording commands/events across Activity recreation.
- One process-lifetime CameraX gateway remains bound while Activity previews attach and detach.
- Activity teardown no longer reports an in-progress recording as failed only because UI was recreated.

Verification:

- Activity recreation unit test proves the bound capture events preserve `VIDEO` mode across UI unbind/rebind.
- Full local unit-test suite passes.
- Debug APK builds successfully.

## Next item: 9 — Process-death and remount recovery proof

Complete destructive ADB/device evidence for force-stop, reboot, physical remount and power-cut cases.

Latest evidence: `docs/local-dev/evidence/build-0.1-item-9-adb-recovery-2026-07-13.md`.
