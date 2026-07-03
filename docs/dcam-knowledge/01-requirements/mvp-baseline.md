# MVP requirement baseline

## Status of requirements

The Confluence `03 - Requirements` folder is empty as of 2026-07-03. There is no formal Functional Requirements document, Non-functional Requirements document, use-case set, user-story set, or DCAM–BDMA Data Contract yet.

The requirements below are therefore a consolidated baseline from Product Vision, Charter, MVP Scope, Roadmap, Development Plan, and the architecture pages. They are useful for orientation, but they do not replace the planned requirement documents or an approved Jira backlog.

## Functional baseline

### Recording

- The operator can start and stop video recording.
- The app exposes a clear recording state.
- Recording works on the target BodyCamera hardware.
- The result is a local media file with associated metadata/status.
- Important start, stop, result, and error events are logged.
- Interruption/background/power-loss behavior requires detailed design; the direction is to preserve or explicitly classify data whenever possible.

### Image capture

- The operator can trigger an image capture.
- The image is stored locally with associated metadata/status.
- Trigger, result, and error events are logged.

### Local storage

- Media, metadata, and logs have a stable, documented, ADB-readable structure.
- Storage availability and free space are checked.
- The app warns, prevents, or stops unsafe recording when capacity is insufficient; the threshold/policy is TBD.
- In-progress/temporary data must be distinguishable from finalized/completed data.
- Exact root, folder names, filenames, cleanup, and retention are not yet contracted.

### Metadata

The architecture expects at least:

| Field | Direction |
|---|---|
| `file_id` | Required unique media identifier; format TBD |
| `file_type` | Required: video/image/audio as applicable |
| `device_id` | Required stable device identifier; source TBD |
| `timestamp` | Required capture/recording time |
| GPS | Optional; present only when valid/available |
| user/operator | Phase 2 foundation; model TBD |
| status | Required source-side state; final vocabulary TBD |
| `schema_version` | Required for compatibility |
| checksum/hash | Valuable for integrity, but still TBD |

The format may be a local database, standalone JSON/CSON/XML-like file, or both. The Data Contract must decide it.

### Device status and capability

- Report or record battery, storage, and GPS availability for MVP.
- Detect camera, microphone, GPS, storage, network, GMS, battery, and potentially USB capabilities before enabling dependent features.
- Missing optional capability must degrade gracefully rather than crash the core flow.

### Logging and diagnostics

- Maintain local application, recording, capture, camera, GPS, storage, update, and crash/error logs as applicable.
- Logs include timestamps and useful context such as device/session/file IDs when safe.
- Do not log passwords, tokens, secrets, raw sensitive metadata, or media content.
- Local logs must exist even when cloud diagnostics are unavailable.
- Rotation/retention is required; the official policy is TBD.

### BDMA integration

- DCAM creates and finalizes source data locally.
- BDMA detects the connected device and initiates ADB discovery/read/sync.
- BDMA imports, validates, maps, indexes, stores, and displays the data.
- BDMA should treat source files as read-only unless a future policy and ADR explicitly permit writes/deletes.
- BDMA must not silently guess or regenerate missing metadata unless the Data Contract explicitly permits it.
- Contract/schema mismatch is a shared DCAM–BDMA concern and requires version handling.

## Non-functional baseline

| Quality | Baseline expectation |
|---|---|
| Availability | Core recording, capture, storage, metadata, logging, and BDMA-readiness work without Internet |
| Reliability | ≥99% record/capture success; no critical corruption or main-flow crash |
| Integrity | Completed/pending/corrupt states are visible; checksum and recovery strategy still TBD |
| Performance | No camera, file, database, or network work blocks the UI thread; recording has priority over diagnostics/cloud |
| Compatibility | Capability-based operation across BodyCamera hardware/firmware and GMS/non-GMS environments |
| Testability | Business flows depend on interfaces, allowing fake platform services |
| Observability | Important actions emit start/result/error diagnostics sufficient for field support |
| Security | Least-required permissions; no hardcoded/logged secrets; media/metadata/update security to be designed |
| Maintainability | Modular layers, vendor SDK isolation, schema versioning, and ADRs for major decisions |

## Acceptance baseline

The MVP Scope lists these acceptance conditions:

1. The app runs on BodyCamera.
2. The user can record video.
3. The user can capture an image.
4. Media is stored in the agreed structure.
5. Metadata is created for each media file.
6. Valid GPS is recorded when available.
7. Main-flow logging works.
8. BDMA can ingest DCAM data.
9. Near-full storage and unavailable GPS do not crash the app.

## Requirement precedence

When sources differ or implementation has moved ahead of the documents:

1. Approved Product/Requirement decision and Data Contract.
2. Approved ADR/Technical Design.
3. Architecture principles and Android Development Standard.
4. Current prototype behavior.

Do not treat current filenames, folders, database tables, Loggly behavior, SOS/audio UI, or build settings as approved product requirements merely because they exist in code.
