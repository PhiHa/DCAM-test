# MVP requirement baseline

## Status of requirements

As of the 2026-07-07 refresh, `03 - Requirements` contains an **Approved 1.2 DCAM-BDMA Data Contract**, a Requirements Home, and ten functional-requirement pages. The first nine were reported as an initial/draft structure; `10 - Android Device Operation Requirements` is Approved 1.0. Non-functional Requirements remain Not Started.

The requirements below remain a consolidated orientation baseline from Product Vision, Charter, MVP Scope, Roadmap, Development Plan, Architecture, and the Data Contract. They do not replace detailed acceptance criteria, an approved Jira backlog, or the source Confluence pages.

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
- Data Contract 1.2 fixes the logical roots, folder names, filename family, import cleanup, and BDMA permissions. Exact physical Android paths, low-space thresholds, retention, and recovery behavior remain for detailed design.

### Metadata

Product/architecture sources still expect per-media identity, device/user/time, optional valid GPS, source state, and compatibility information. Data Contract 1.2 adds these binding constraints:

| Concern | Current authority/direction |
|---|---|
| File association | Filename contains CameraID, fixed-six-character UserID, date, and time; exact collision/unique-ID policy remains TBD |
| Media metadata | Embedded in the media file when supported; standalone per-media JSON is not part of contract 1.2 |
| Important/encrypted state | Filename suffixes `_IMP`, `_enc`, or `_IMP_enc` |
| GPS and source lifecycle | Still require exact embedded fields, validation, and persistence design |
| Database version | `dcam.db` needs an agreed schema-version mechanism |
| Integrity | Optional same-basename `.md5` applies only to `.mp4`; image/audio have no MD5 sidecar |

The exact embedded metadata fields/encoding and SQLite schema remain open. Implementations must not introduce a standalone media JSON contract without updating Data Contract 1.2.

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
- BDMA must not modify source media, embedded metadata, MD5 content, `Temp`, or `logs.txt`.
- BDMA may update `dcam_config.cson` for device information and may read/write/update all of `dcam.db`, subject to schema, corruption, locking, and concurrent-write safeguards.
- BDMA may delete successfully imported source media under the contract cleanup matrix. Missing-MD5 MP4 deletion requires confirmation for each file.
- BDMA must not silently guess or regenerate missing metadata unless a later contract explicitly permits it.
- Contract/schema mismatch is a shared DCAM–BDMA concern and requires version handling.

## Non-functional baseline

| Quality | Baseline expectation |
|---|---|
| Availability | Core recording, capture, storage, metadata, logging, and BDMA-readiness work without Internet |
| Reliability | ≥99% record/capture success; no critical corruption or main-flow crash |
| Integrity | MP4-only optional MD5/import outcomes are decided; DCAM finalize validation, persisted lifecycle, and recovery remain TBD |
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
